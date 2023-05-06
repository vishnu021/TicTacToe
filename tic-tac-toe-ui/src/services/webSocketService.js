import {toast} from "react-toastify";
import SockJS from "sockjs-client";
import Stomp from "stompjs";

const webSocketEndpoint = process.env.REACT_APP_API_URL;
const subscriptionTopic = process.env.REACT_APP_SUBSCRIPTION_TOPIC;
const startGameTopic = process.env.REACT_APP_GAME_START_TOPIC;
const registrationTopic = process.env.REACT_APP_REGISTRATION_ENDPOINT;

const initialiseWebsocket = async () => {
    const socket = new SockJS(webSocketEndpoint);
    const client = Stomp.over(socket);
    client.debug = null;
    await new Promise((resolve, reject) => {
        client.connect({}, () => resolve(client), (error) => reject(error));
    });

    return client;
}



const subscribeToTopic = (client, handleGameStart) => {
    console.log("subscribing to start game topic", new Date());
    client.subscribe(startGameTopic, (message) => {
        const response = JSON.parse(message.body);
        console.log("starting game now");
        handleGameStart(response);
    }, (error) => {
        console.error(`Error subscribing to start game channel :  ${webSocketEndpoint}${startGameTopic}`, error);
        toast.error('Failed to connect to server');
    });
}

const subscribeToGame = (client, stepFunction) => {
    client.subscribe(subscriptionTopic, stepFunction, (error) => {
        console.error(`Error subscribing to ${subscriptionTopic} channel:`, error);
        console.error(`Error subscribing to step game channel :  ${webSocketEndpoint}${startGameTopic}`, error);
        toast.error('Failed to connect to server');
    });
}

const registerNewUser = (client, userName) => {
    const gameStarterPayload = {
        "user": userName,
        "createNewRoom": false
    };
    console.log(`Registering new user ${JSON.stringify(gameStarterPayload)} to ${webSocketEndpoint}`);
    client.send(registrationTopic, {}, JSON.stringify(gameStarterPayload));
}

export default {
    initialise: initialiseWebsocket,
    subscribe: subscribeToTopic,
    subscribeToGame: subscribeToGame,
    register: registerNewUser
}