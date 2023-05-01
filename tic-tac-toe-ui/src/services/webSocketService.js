import {toast} from "react-toastify";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const webSocketEndpoint = "http://localhost:8080/ws"
const subscriptionTopic = '/user/topic/game-ws'
const startGameTopic = '/user/topic/game-start'

const initialiseWebsocket = async () => {
    const socket = new SockJS(webSocketEndpoint);
    const client = Stomp.over(socket);
    client.debug = console.log;
    await new Promise(resolve => client.connect({}, resolve));

    client.onclose = () => {
        console.log("Connection closed");
        toast.error('Failed to connect to server');
    };

    return client;
}

const subscribeToTopic = (client, handleGameStart) => {
    client.subscribe(startGameTopic, (message) => {
        const response = JSON.parse(message.body);
        console.log('response:', response);
        handleGameStart(response);
    }, (error) => {
        console.error(`Error subscribing to ${startGameTopic} channel:`, error);
        toast.error('Failed to connect to server');
    });
}

const subscribeToGame = (client, stepFunction) => {
    client.subscribe(subscriptionTopic, stepFunction, (error) => {
        console.error(`Error subscribing to ${subscriptionTopic} channel:`, error);
        toast.error('Failed to connect to server');
    });
}

const registerNewUser = (client, userName) => {
    const gameStarterPayload = {
        "user": userName,
        "createNewRoom": false
    };
    console.log("registering new user", gameStarterPayload);
    client.send('/app/register', {}, JSON.stringify(gameStarterPayload));
}

export default {
    initialise: initialiseWebsocket,
    subscribe: subscribeToTopic,
    subscribeToGame: subscribeToGame,
    register: registerNewUser
}