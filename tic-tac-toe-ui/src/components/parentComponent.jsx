import React from 'react';
import GameBoard from "./GameBoard";
import {useLocation, useNavigate} from "react-router-dom";

function ParentComponent(props) {
    const location = useLocation();
    const navigate = useNavigate();

    if(!location || !location.state) {
        const timeoutId = setTimeout(() => {
            navigate('/');
        }, 500);
        return;
    }
    return (
        <GameBoard
            gameDetails={location.state}
        />
    );
}

export default ParentComponent;