import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

// Use origin dynamically - fallback to env variable for development
const getWebSocketUrl = () => {
    const wsPath = import.meta.env.VITE_WS_PATH || '/ws';
    // In production, use the same origin as the page
    if (import.meta.env.PROD) {
        return `${window.location.origin}${wsPath}`;
    }
    // In development, use env variable or default to current origin
    return import.meta.env.VITE_API_URL || `${window.location.origin}${wsPath}`;
};

const subscriptionTopic = import.meta.env.VITE_SUBSCRIPTION_TOPIC;
const startGameTopic = import.meta.env.VITE_GAME_START_TOPIC;
const registrationTopic = import.meta.env.VITE_REGISTRATION_ENDPOINT;

const initialiseWebsocket = async () => {
    const webSocketEndpoint = getWebSocketUrl();

    const client = new Client({
        webSocketFactory: () => new SockJS(webSocketEndpoint),
        debug: () => {}, // Disable debug logs
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
    });

    await new Promise((resolve, reject) => {
        client.onConnect = () => resolve(client);
        client.onStompError = (frame) => {
            reject(new Error(frame.headers['message'] || 'STOMP error'));
        };
        client.onWebSocketError = () => {
            reject(new Error('WebSocket connection error'));
        };
        client.activate();
    });

    return client;
};

const subscribeToTopic = (client, handleGameStart) => {
    return client.subscribe(startGameTopic, (message) => {
        const response = JSON.parse(message.body);
        handleGameStart(response);
    });
};

const subscribeToGame = (client, stepFunction) => {
    return client.subscribe(subscriptionTopic, stepFunction);
};

const registerNewUser = (client, userName) => {
    const gameStarterPayload = {
        "user": userName,
        "createNewRoom": false
    };
    client.publish({
        destination: registrationTopic,
        body: JSON.stringify(gameStarterPayload)
    });
};

const webSocketService = {
    initialise: initialiseWebsocket,
    subscribe: subscribeToTopic,
    subscribeToGame: subscribeToGame,
    register: registerNewUser
};

export default webSocketService;
