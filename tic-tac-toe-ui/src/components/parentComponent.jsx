import React from 'react';
import GameBoard from "./GameBoard";
import {useLocation} from "react-router-dom";

function ParentComponent(props) {
    const location = useLocation();
    return (
        <GameBoard
            gameDetails={location.state}
        />
    );
}

export default ParentComponent;