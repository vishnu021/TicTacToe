import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faCircle, faSquare, faTimes} from '@fortawesome/free-solid-svg-icons'

function TickSpace({tickSpace, isCrossPlayer}) {

    const getCircleColor = () => {
        if(isCrossPlayer()) {
            return "opponent"
        }
        return "player";
    }

    const getCrossColor = () => {
        if(isCrossPlayer()) {
            return "player"
        }
        return "opponent";
    }

    const getSymbol = () => {
        const {crossed, clicked} = tickSpace;
        if(clicked) {
            return crossed ?
                <FontAwesomeIcon className={getCrossColor()} icon={faTimes} size="2x"/>:
                <FontAwesomeIcon className={getCircleColor()} icon={faCircle} size="2x"/>;
        }
        return <FontAwesomeIcon icon={faSquare} style={{ color: 'transparent'}} size="2x" />;
    }

    return (
        <div
            style={{ width: '100%', height: '100%', padding: 0, border: 'none' }}
            className="p-4" >
            {getSymbol()}
            </div>
    );
}

export default TickSpace;
