import React from 'react';
import GameBoard from "./GameBoard";
import {useLocation, useNavigate} from "react-router-dom";

function ParentComponent(props) {
    const location = useLocation();
    const navigate = useNavigate();

    if(!location || !location.state) {
        setTimeout(() => {
            navigate('/');
        }, 500);
        return null;
    }
    return (
        <GameBoard
            gameDetails={location.state}
        />
    );
}

export default ParentComponent;