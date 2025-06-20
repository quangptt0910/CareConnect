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

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {logger} = require("firebase-functions");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");

// Initialize Firebase Admin SDK
initializeApp();

// Define Firestore database instance
const firestore = getFirestore();

exports.sendChatNotification = onDocumentCreated(
    {
      document: "chat_notifications/{docId}",
      region: "europe-central2",
      maxInstances: 10, // Control concurrency
      timeoutSeconds: 30, // Set timeout
    },
    async (event) => {
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
