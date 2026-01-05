package it.auties.analyzer

import com.github.auties00.cobalt.node.Node

fun onMessageSent(node: Node) {
    println("Sent Binary Message: $node")
}

fun onMessageReceived(node: Node) {
    println("Received Binary Message $node")
}