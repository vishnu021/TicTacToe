import React, {createContext, useEffect, useState, useCallback} from 'react';
import websocket from "../services/webSocketService";
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';

export const StompContext = createContext(null);

function Parent(props) {
    const [stompClient, setStompClient] = useState(null);
    const [isConnecting, setIsConnecting] = useState(true);

    const initialise = useCallback(async () => {
        setIsConnecting(true);
        try {
            const client = await websocket.initialise();
            setStompClient(client);
        } catch (error) {
            toast.error('Unable to connect to server');
        } finally {
            setIsConnecting(false);
        }
    }, []);

    useEffect(() => {
        initialise();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [initialise]);

    return (
       <React.Fragment>
           <ToastContainer />
           <StompContext.Provider value={{ stompClient, isConnecting }} >
               {props.children}
           </StompContext.Provider>
       </React.Fragment>
    );
}

export default Parent;