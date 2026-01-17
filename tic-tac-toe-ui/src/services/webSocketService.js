import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

// Use origin dynamically - fallback to env variable for development
const getWebSocketUrl = () => {
    const wsPath = process.env.REACT_APP_WS_PATH || '/ws';
    // In production, use the same origin as the page
    if (process.env.NODE_ENV === 'production') {
        return `${window.location.origin}${wsPath}`;
    }
    // In development, use env variable or default to current origin
    return process.env.REACT_APP_API_URL || `${window.location.origin}${wsPath}`;
};

const subscriptionTopic = process.env.REACT_APP_SUBSCRIPTION_TOPIC;
const startGameTopic = process.env.REACT_APP_GAME_START_TOPIC;
const registrationTopic = process.env.REACT_APP_REGISTRATION_ENDPOINT;

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
        client.onWebSocketError = (event) => {
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
