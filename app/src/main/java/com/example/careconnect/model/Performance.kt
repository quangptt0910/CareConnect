package com.example.careconnect.model


import com.google.firebase.perf.trace
import com.google.firebase.perf.metrics.Trace

/**
 * Trace a block with Firebase performance.
 *
 * Supports both suspend and regular methods.
 */
inline fun <T> trace(name: String, block: Trace.() -> T): T = Trace.create(name).trace(block)
