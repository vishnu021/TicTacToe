import React, {createContext, useEffect, useState} from 'react';
import websocket from "../services/webSocketService";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

export const StompContext = createContext(null);

function Parent(props) {
    const [stompClient, setStompClient] = useState(null);

    console.log("loading parent component");
    const initialise = async () => {
        const client = await websocket.initialise().then((client) => {
            console.log("connected to client : ", client);
            setStompClient(client);
        }).catch((error) => {
            toast.error('Unable to connect');
        });

    }

    useEffect(() => {
        initialise();
        return () => { };
    }, []);

    return (
       <React.Fragment>
           <ToastContainer />
           <StompContext.Provider value={{ stompClient }} >
               {props.children}
           </StompContext.Provider>
       </React.Fragment>
    );
}

export default Parent;