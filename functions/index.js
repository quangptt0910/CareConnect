/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const {onCall, HttpsError} = require("firebase-functions/v2/https");
const {onDocumentCreated, onDocumentUpdated} = require("firebase-functions/v2/firestore");
const {onSchedule} = require("firebase-functions/v2/scheduler");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
const {getMessaging} = require("firebase-admin/messaging");
const {setGlobalOptions} = require("firebase-functions/v2");
// Initialize Firebase Admin SDK
admin.initializeApp();


setGlobalOptions({region: "europe-central2"});


exports.createDoctor = onCall(
    async (request) => {
    // 1. Authentication Check
      if (!request.auth) {
        logger.warn("createDoctor call attempt without authentication.");
        throw new HttpsError(
            "unauthenticated",
            "Authentication is required to perform this action.",
        );
      }

      const adminUid = request.auth.uid;
      const adminEmail = request.auth.token.email || "[email not available]";
      logger.info(
          `createDoctor called by authenticated admin: ${adminEmail} (UID: ${adminUid})`,
      );

      // 2. Authorization Check
      try {
        const adminDocRef = admin.firestore().collection("admins").doc(adminUid);
        const adminDoc = await adminDocRef.get();

        if (!adminDoc.exists) {
          logger.error(
              "Permission denied for createDoctor: User is not listed as an admin.",
              {
                adminUid: adminUid,
                adminEmail: adminEmail,
              },
          );
          throw new HttpsError(
              "permission-denied",
              "You do not have the required permissions to create doctor accounts.",
          );
        }
      } catch (error) {
        logger.error("Failed to verify admin status due to Firestore error.", {
          adminUid: adminUid,
          adminEmail: adminEmail,
          error: error,
        });
        throw new HttpsError(
            "internal",
            "Could not verify admin status. Please try again later.",
        );
      }

      // 3. Input Validation
      const {email, password, doctorData} = request.data;

      if (
        !email ||
      typeof email !== "string" ||
      !password ||
      typeof password !== "string" ||
      !doctorData ||
      typeof doctorData !== "object" ||
      Array.isArray(doctorData)
      ) {
        logger.error("Invalid arguments received for createDoctor.", {
          adminUid: adminUid,
          receivedData: {
            email: email,
            hasPassword: !!password,
            doctorData: doctorData,
          },
        });
        throw new HttpsError(
            "invalid-argument",
            "Missing or invalid parameters. Ensure 'email' (string), 'password' (string), and 'doctorData' (object) are provided correctly.",
        );
      }

      // Additional validation for required doctor fields
      const requiredFields = ["name", "surname", "phone", "address", "specialization", "experience"];
      for (const field of requiredFields) {
        if (!doctorData[field]) {
          throw new HttpsError(
              "invalid-argument",
              `Missing required field: ${field}`,
          );
        }
      }

      // Validate experience is a number
      if (typeof doctorData.experience !== "number") {
        throw new HttpsError(
            "invalid-argument",
            "Experience must be a number (year).",
        );
      }

      // Create Auth user and Firestore document
      let userRecord;
      try {
        userRecord = await admin.auth().createUser({
          email: email,
          password: password,
        });

        logger.info("Firebase Authentication user created successfully.", {
          doctorId: userRecord.uid,
          doctorEmail: email,
          createdByAdminUid: adminUid,
        });
      } catch (error) {
        logger.error("Error creating Firebase Authentication user.", {
          doctorEmail: email,
          createdByAdminUid: adminUid,
          errorCode: error.code,
          errorMessage: error.message,
        });

        if (error.code === "auth/email-already-exists") {
          throw new HttpsError(
              "already-exists",
              `The email address '${email}' is already in use by another account.`,
          );
        } else if (error.code === "auth/invalid-password") {
          throw new HttpsError(
              "invalid-argument",
              "Password is invalid. It must be a string with at least 6 characters.",
          );
        }

        throw new HttpsError(
            "internal",
            "Failed to create the authentication record for the doctor.",
        );
      }

      // Create Firestore document
      const doctorId = userRecord.uid;
      try {
        const doctorDocRef = admin
            .firestore()
            .collection("doctors")
            .doc(doctorId);

        // Prepare the data for Firestore with proper field mapping
        const finalDoctorData = {
          name: doctorData.name,
          surname: doctorData.surname,
          phone: doctorData.phone,
          address: doctorData.address,
          specialization: doctorData.specialization,
          experience: doctorData.experience,
          profilePhoto: doctorData.profilePhoto || "",
          email: email,
          role: "DOCTOR", // Store as string to match enum
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
        };

        await doctorDocRef.set(finalDoctorData);

        logger.info("Firestore doctor document created successfully.", {
          doctorId: doctorId,
          createdByAdminUid: adminUid,
        });

        // 5. Create welcome email document for the email extension
        try {
          await admin.firestore().collection("mail").add({
            to: email,
            template: {
              name: "doctor-welcome", // Template name you'll create
              data: {
                doctorName: `${doctorData.name} ${doctorData.surname}`,
                email: email,
                specialization: doctorData.specialization,
                temporaryPassword: password,
              // Add any other data you want in the email
              },
            },
          });

          logger.info("Welcome email queued successfully.", {
            doctorId: doctorId,
            doctorEmail: email,
          });
        } catch (emailError) {
        // Log error but don't fail the entire operation
          logger.error("Failed to queue welcome email.", {
            doctorId: doctorId,
            doctorEmail: email,
            error: emailError,
          });
        }

        // 6. Success Response
        return {
          message: "Doctor account created successfully.",
          doctorId: doctorId,
        };
      } catch (error) {
        logger.error(
            "CRITICAL: Failed to create Firestore doctor document after Auth user was created.",
            {
              doctorId: doctorId,
              doctorEmail: email,
              createdByAdminUid: adminUid,
              error: error,
            },
        );

        // Cleanup: Delete the orphaned Auth user
        try {
          logger.warn(`Attempting to delete orphaned Auth user: ${doctorId}`);
          await admin.auth().deleteUser(doctorId);
          logger.info(`Successfully deleted orphaned Auth user: ${doctorId}`);
        } catch (cleanupError) {
          logger.error(
              `CRITICAL: Failed to delete orphaned Auth user after Firestore failure.`,
              {
                doctorId: doctorId,
                cleanupError: cleanupError,
              },
          );
        }

        throw new HttpsError(
            "internal",
            "Failed to save doctor details after account creation. The operation was rolled back. Please try again.",
        );
      }
    },
);

exports.handleNotificationTrigger = onDocumentCreated(
    {
      document: "notification_triggers/{triggerId}",
      region: "europe-central2",
    },
    async (event) => {
      try {
        if (!event.data || !event.data.exists) {
          console.error("🚫 Invalid event data", event);
          return;
        }

        const data = event.data.data();

        // Skip if already processed
        if (data.processed) return null;

        const {type, appointmentId, patientId, doctorId, patientName, doctorName, appointmentDate, startTime} = data;

        // Validate required fields
        if (!type || !appointmentId || !patientId || !doctorId) {
          console.error("Missing required fields in notification trigger:", data);
          await event.data.ref.update({
            processed: true,
            error: "Missing required fields",
          });
          return null;
        }

        let recipientId; let title; let body;

        // Determine recipient and message based on notification type
        switch (type) {
          case "PENDING":
            recipientId = doctorId;
            title = "New Appointment Pending";
            body = `${patientName} has requested an appointment on ${appointmentDate} at ${startTime}`;
            break;

          case "CONFIRMED":
            recipientId = patientId;
            title = "Appointment Confirmed";
            body = `Dr. ${doctorName} has confirmed your appointment on ${appointmentDate} at ${startTime}`;
            break;

          case "CANCELED":
            recipientId = patientId;
            title = "Appointment Declined";
            body = `Dr. ${doctorName} has declined/canceled your appointment request for ${appointmentDate} at ${startTime}`;
            break;

          case "COMPLETED":
            recipientId = patientId;
            title = "Appointment Completed";
            body = `Your appointment with Dr. ${doctorName} on ${appointmentDate} has been marked as completed`;
            break;

          case "NO_SHOW":
            recipientId = patientId;
            title = "Missed Appointment";
            body = `You missed your appointment with Dr. ${doctorName} on ${appointmentDate} at ${startTime}`;
            break;

          default:
            console.log("Unknown notification type:", type);
            return null;
        }

        // Get recipient's FCM token
        const tokenDoc = await admin.firestore()
            .collection("user_tokens")
            .doc(recipientId)
            .get();

        if (!tokenDoc.exists) {
          console.log("No FCM token found for user:", recipientId);
          // Don't mark as processed immediately - allow for retry
          await event.data.ref.update({
            processed: false,
            error: "No FCM token found",
            retryCount: (data.retryCount || 0) + 1,
            lastAttempt: admin.firestore.FieldValue.serverTimestamp(),
          });
          return null;
        }

        const fcmToken = tokenDoc.data().fcmToken;

        // Send notification
        const message = {
          token: fcmToken,
          notification: {
            title: title,
            body: body,
          },
          data: {
            type: type,
            appointmentId: appointmentId,
            userType: recipientId === patientId ? "patient" : "doctor",
            click_action: "FLUTTER_NOTIFICATION_CLICK",
          },
          android: {
            priority: "high",
            notification: {
              channelId: "appointment_notifications",
              priority: "high",
              defaultSound: true,
              click_action: "FLUTTER_NOTIFICATION_CLICK",
            },
          },
          apns: {
            payload: {
              aps: {
                alert: {
                  title: title,
                  body: body,
                },
                sounds: "default",
                badge: 1,
              },
            },
          },
        };

        const response = await admin.messaging().send(message);
        console.log("Successfully sent notification:", response);

        // Mark as processed and schedule reminder if it's a new appointment
        const updateData = {processed: true, sentAt: admin.firestore.FieldValue.serverTimestamp(), messageId: response};

        if (type === "CONFIRMED") {
        // Schedule reminder notification
          await scheduleReminderNotification(appointmentId, appointmentDate, startTime, patientId, doctorId, patientName, doctorName);
          updateData.reminderScheduled = true;
        }

        await event.data.ref.update(updateData);

        return null;
      } catch (error) {
        console.error("Error handling notification trigger:", error);

        // Mark as processed with error
        await event.data.ref.update({
          processed: true,
          error: error.message,
          errorAt: admin.firestore.FieldValue.serverTimestamp(),
        });

        return null;
      }
    });

// 2. Helper function to schedule reminder notifications
/**
 * Schedules a reminder notification for a given appointment.
 *
 * @param {string} appointmentId   Firestore document ID of the appointment
 * @param {Date}   appointmentDate The date of the appointment
 * @param {string} startTime       The start time (e.g., "14:00")
 * @param {string} patientId       UID of the patient
 * @param {string} doctorId        UID of the doctor
 * @param {string} patientName     Full name of the patient
 * @param {string} doctorName      Full name of the doctor
 * @return {Promise<void>}        Resolves when the notification is scheduled
 */
async function scheduleReminderNotification(appointmentId, appointmentDate, startTime, patientId, doctorId, patientName, doctorName) {
  try {
    // Parse appointment date and calculate reminder time (1 day before)
    const appointmentDateTime = new Date(`${appointmentDate} ${startTime}`);
    const reminderDateTime = new Date(appointmentDateTime.getTime() - (24 * 60 * 60 * 1000));

    // Only schedule if reminder time is in the future
    if (reminderDateTime <= new Date()) {
      console.log("Appointment is too soon for reminder scheduling");
      return;
    }

    const scheduledNotification = {
      appointmentId: appointmentId,
      appointmentDate: appointmentDate,
      startTime: startTime,
      patientId: patientId,
      doctorId: doctorId,
      patientName: patientName,
      doctorName: doctorName,
      reminderDateTime: admin.firestore.Timestamp.fromDate(reminderDateTime),
      status: "scheduled",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    };

    await admin.firestore().collection("scheduled_notifications").add(scheduledNotification);
    console.log("Reminder notification scheduled for:", reminderDateTime);
  } catch (error) {
    console.error("Error scheduling reminder:", error);
  }
}

// 3. Scheduled function to send reminder notifications (runs every hour)
exports.sendScheduledReminders = onSchedule(
    {schedule: "0 * * * *", timeZone: "UTC"},
    async () => {
      try {
        const now = admin.firestore.Timestamp.now();
        const oneHourFromNow = admin.firestore.Timestamp.fromMillis(now.toMillis() + (60 * 60 * 1000));

        // Get scheduled notifications that should be sent within the next hour
        const query = await admin.firestore()
            .collection("scheduled_notifications")
            .where("status", "==", "scheduled")
            .where("reminderDateTime", ">=", now)
            .where("reminderDateTime", "<=", oneHourFromNow)
            .get();

        if (query.empty) {
          console.log("No reminders to send");
          return null;
        }

        const batch = admin.firestore().batch();
        const notifications = [];

        for (const doc of query.docs) {
          const data = doc.data();

          // Check if appointment still exists and is confirmed
          const appointmentDoc = await admin.firestore()
              .collection("appointments")
              .doc(data.appointmentId)
              .get();

          if (!appointmentDoc.exists) {
            // Appointment was deleted, cancel reminder
            batch.update(doc.ref, {status: "cancelled", reason: "Appointment not found"});
            continue;
          }

          const appointment = appointmentDoc.data();
          if (appointment.status !== "CONFIRMED") {
            // Appointment is not confirmed, cancel reminder
            batch.update(doc.ref, {status: "cancelled", reason: `Appointment status: ${appointment.status}`});
            continue;
          }

          // Get FCM tokens for both patient and doctor
          const [patientTokenDoc, doctorTokenDoc] = await Promise.all([
            admin.firestore().collection("user_tokens").doc(data.patientId).get(),
            admin.firestore().collection("user_tokens").doc(data.doctorId).get(),
          ]);

          // Create notification messages
          if (patientTokenDoc.exists) {
            notifications.push({
              token: patientTokenDoc.data().fcmToken,
              notification: {
                title: "Appointment Reminder",
                body: `Don't forget your appointment with Dr. ${data.doctorName} tomorrow at ${data.startTime}`,
              },
              data: {
                type: "REMINDER",
                appointmentId: data.appointmentId,
                userType: "patient",
              },
            });
          }

          if (doctorTokenDoc.exists) {
            notifications.push({
              token: doctorTokenDoc.data().fcmToken,
              notification: {
                title: "Appointment Reminder",
                body: `You have an appointment with ${data.patientName} tomorrow at ${data.startTime}`,
              },
              data: {
                type: "REMINDER",
                appointmentId: data.appointmentId,
                userType: "doctor",
              },
            });
          }

          // Mark notification as sent
          batch.update(doc.ref, {
            status: "sent",
            sentAt: admin.firestore.FieldValue.serverTimestamp(),
          });
        }

        // Send all notifications
        if (notifications.length > 0) {
          const response = await admin.messaging().sendAll(notifications);
          console.log(`Successfully sent ${response.successCount} reminder notifications`);

          if (response.failureCount > 0) {
            console.log(`Failed to send ${response.failureCount} notifications`);
            response.responses.forEach((resp, idx) => {
              if (!resp.success) {
                console.error(`Failed to send notification ${idx}:`, resp.error);
              }
            });
          }
        }

        // Commit batch updates
        await batch.commit();

        return null;
      } catch (error) {
        console.error("Error in sendScheduledReminders:", error);
        return null;
      }
    });

// 4. Firestore Trigger - Handle appointment status changes for reminder cancellation
exports.handleAppointmentStatusChange = onDocumentUpdated(
    {
      document: "appointments/{appointmentId}",
      region: "europe-central2",
    },
    async (event) => {
      try {
        const before = event.data.before.data();
        const after = event.data.after.data();
        const appointmentId = event.params.appointmentId;

        if (!event.data || !event.data.before || !event.data.after) {
          console.error("Invalid change event", event);
          return;
        }

        // If appointment was cancelled or completed, cancel any scheduled reminders
        if (before.status !== after.status &&
          (after.status === "CANCELED" || after.status === "COMPLETED")) {
          const remindersQuery = await admin.firestore()
              .collection("scheduled_notifications")
              .where("appointmentId", "==", appointmentId)
              .where("status", "==", "scheduled")
              .get();

          if (!remindersQuery.empty) {
            const batch = admin.firestore().batch();
            remindersQuery.docs.forEach((doc) => {
              batch.update(doc.ref, {
                status: "cancelled",
                reason: `Appointment ${after.status.toLowerCase()}`,
                cancelledAt: admin.firestore.FieldValue.serverTimestamp(),
              });
            });

            await batch.commit();
            console.log(`Cancelled ${remindersQuery.docs.length} scheduled reminders for appointment ${appointmentId}`);
          }
        }

        return null;
      } catch (error) {
        console.error("Error handling appointment status change:", error);
        return null;
      }
    },
);

// 5. Cleanup function - runs weekly to remove old processed notifications
exports.cleanupOldNotifications = onSchedule(
    {schedule: "0 2 * * 0", timeZone: "UTC"},
    async () => {
      try {
        const oneWeekAgo = admin.firestore.Timestamp.fromMillis(Date.now() - (7 * 24 * 60 * 60 * 1000));

        // Cleanup processed notification triggers (older than 1 week)
        const oldTriggers = await admin.firestore()
            .collection("notification_triggers")
            .where("processed", "==", true)
            .where("timestamp", "<", oneWeekAgo)
            .limit(500) // Process in batches
            .get();

        // Cleanup old scheduled notifications (older than 1 week)
        const oldScheduled = await admin.firestore()
            .collection("scheduled_notifications")
            .where("createdAt", "<", oneWeekAgo)
            .limit(500)
            .get();

        const batch = admin.firestore().batch();

        oldTriggers.docs.forEach((doc) => batch.delete(doc.ref));
        oldScheduled.docs.forEach((doc) => batch.delete(doc.ref));

        if (oldTriggers.docs.length > 0 || oldScheduled.docs.length > 0) {
          await batch.commit();
          console.log(`Cleaned up ${oldTriggers.docs.length} old notification triggers and ${oldScheduled.docs.length} old scheduled notifications`);
        }

        return null;
      } catch (error) {
        console.error("Error cleaning up notifications:", error);
        return null;
      }
    });


exports.sendChatNotification = onDocumentCreated(
    {
      document: "chat_notifications/{docId}",
      region: "europe-central2",
      maxInstances: 10, // Control concurrency
      timeoutSeconds: 30, // Set timeout
    },
    async (event) => {
      const firestore = admin.firestore();
      // Extract document data
      const snapshot = event.data;
      if (!snapshot) {
        logger.error("No data associated with the event");
        return;
      }

      const data = snapshot.data();
      const {chatId, message, senderId, senderName, recipientId} = data;
      logger.log(`Processing notification for chat ${chatId}`, data);

      // Skip self-notifications
      if (senderId === recipientId) {
        logger.log("Skipping self-notification");
        await snapshot.ref.delete();
        return;
      }

      try {
      // Get recipient's FCM tokens
        const tokensDoc = await firestore.collection("user_tokens")
            .doc(recipientId)
            .get();

        if (!tokensDoc.exists) {
          logger.log(`No tokens found for recipient: ${recipientId}`);
          await snapshot.ref.delete();
          return;
        }

        const tokenData = tokensDoc.data();
        const token = tokenData.fcmToken;

        if (!token) {
          logger.error(`Token missing for recipient: ${recipientId}`);
          await snapshot.ref.delete();
          return;
        }

        // Prepare notification payload
        const payload = {
          notification: {
            title: `New message from ${senderName}`,
            body: message.length > 100 ?
                `${message.substring(0, 100)}...` :
                message,
          },
          data: {
            type: "CHAT_MESSAGE",
            chatId: chatId,
            senderId: senderId,
            senderName: senderName,
            recipientId: recipientId,
          },
        };

        // Send notifications
        const response = await getMessaging().send({
          token: token,
          ...payload,
        });
        logger.log(`Sent notifications to ${tokens.length} devices`, response);
        // Delete the trigger document
        await snapshot.ref.delete();
        logger.log("Notification processed successfully");
      } catch (error) {
        logger.error("Error in sendChatNotification", error);
      }
    },
);
