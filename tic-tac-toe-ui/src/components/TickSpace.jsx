import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import {faCircle, faSquare, faTimes} from '@fortawesome/free-solid-svg-icons'

function TickSpace({tickSpace}) {

    const getSymbol = () => {
        const {id, crossed, clicked} = tickSpace;
        if(clicked) {
            return crossed ?
                <FontAwesomeIcon icon={faTimes} size="2x"/>:
                <FontAwesomeIcon icon={faCircle} size="2x"/>;
        }
        return <FontAwesomeIcon icon={faSquare} style={{ color: 'transparent'}} size="2x" />;
    }

    return (
        <div
            style={{ width: '100%', height: '100%', padding: 0, border: 'none' }}
            className="m-3" >
            {getSymbol()}
            </div>
    );
}

export default TickSpace;
