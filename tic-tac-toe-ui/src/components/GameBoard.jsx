import React, {useContext, useEffect, useState} from 'react';
import websocket from '../services/webSocketService'
import TickSpaces from "./TickSpaces";
import CelebrationAnimation from "./CelebrationAnimation";
import {StompContext} from "./parent";
import {useNavigate, useParams} from "react-router-dom";

function GameBoard({gameDetails}) {
    const [playerName, setPlayerName] = useState("");
    const [playerId, setPlayerId] = useState("");
    const [opponentName, setOpponentName] = useState("");
    const [tickSpaces, setTickSpaces] = useState([]);
    const [crossPlayerId, setCrossPlayerId] = useState("");
    const [activePlayerId, setActivePlayerId] = useState("");
    const [opponentId, setOpponentId] = useState("");
    const [gameActive, setGameActive] = useState(true);
    const [winningPlayer, setWinningPlayer] = useState(null);
    const {stompClient, setStompClient } = useContext(StompContext);
    const { pageId } = useParams();
    const navigate = useNavigate();
    const backToPool = () => {
        websocket.register(stompClient, playerName);
        const payload = { state: { userName: playerName } };
        navigate('/pool', payload);
    }


    const parseGameDetails = (gameDetails) => {
        const { playerName, playerId, opponentName, opponentId, board, winningPlayer } = gameDetails;
        const {tickSpaces, crossPlayerId, activePlayerId} = board;
        setPlayerName(playerName);
        setPlayerId(playerId);
        setOpponentName(opponentName);
        setOpponentId(opponentId);
        setTickSpaces(tickSpaces);
        setActivePlayerId(activePlayerId)
        setCrossPlayerId(crossPlayerId)
        setWinningPlayer(winningPlayer);
    }
    const isCrossTick = () => {
        return playerId === crossPlayerId;
    }

    const sendNextStep = () => {
        const stepPayload = {
            "playerName": playerName,
            "playerId": playerId,
            "opponentName": opponentName,
            "opponentId": opponentId,
            "board": {
                "roomId": pageId,
                "tickSpaces": tickSpaces,
                "crossPlayerId": crossPlayerId,
                "activePlayerId": activePlayerId
            }
        }
        stompClient.send('/app/step', {}, JSON.stringify(stepPayload));
    }

    const handleClick = tickSpace =>  {
        if(activePlayerId === playerId && gameActive && !winningPlayer) {
            const boardTickSpaces = tickSpaces
            const index = boardTickSpaces.indexOf(tickSpace);
            if (!boardTickSpaces[index].clicked) {
                boardTickSpaces[index].clicked = true;
                boardTickSpaces[index].crossed = isCrossTick();
                setTickSpaces(boardTickSpaces)
                sendNextStep();
            }
        }
    }

    const getTickSpaceClass = () => {
        console.log(`playerId : ${playerId}, active player : ${activePlayerId}`);
        if(activePlayerId===playerId) {
            return "active-player";
        } else {
            return "inactive-player";
        }
    }


    useEffect(() => {
        parseGameDetails(gameDetails);
    }, []);

    const gameStepHandler = (message) => {
        const response = JSON.parse(message.body);
        parseGameDetails(response);
    }

    const getGameCompleteMessage = () => {
        if(winningPlayer) {
            if(winningPlayer === playerId)
                return <p>Congrats {playerName} you won!!!</p>
            else
                return <p>Better Luck Next Time</p>
        }
        return <p></p>;
    }
    const isCrossPlayer = () => {
        return playerId === crossPlayerId;
    }

    const getNameStyle = () => {
        const styles = {textAlign: "right"}
        if(playerId===activePlayerId) {
            return {...styles,  fontSize: "25", fontWeight: "bold"}
        }
        return styles;
    }

    const getGamePlayMessage = () => {
        if(playerId===activePlayerId)
        return "your turn ";
        else return "";
    }

    websocket.subscribeToGame(stompClient, gameStepHandler);
    return (
        <div className="row m-2 game-font">
            <div className="col-md-4 offset-md-4 col-sm-12">
                <CelebrationAnimation
                    animationStarted={winningPlayer === playerId}
                    duration={5000}
                    numberOfParticles={250} />
                <h2 className="m-3" style={{textAlign: "center"}}>TIC TAC TOE</h2>
                <p style={{textAlign: "center"}}>room : {pageId}</p>
                <div style={{textAlign: "left"}} className="opponent m-2">
                    {opponentName}
                </div>
                <div className={getTickSpaceClass()}>
                    <TickSpaces
                        tickSpaces={tickSpaces}
                        handleClick={handleClick}
                        isCrossPlayer={isCrossPlayer}
                    />
                </div>
                <div className="player m-2" style={getNameStyle()}>
                    {getGamePlayMessage()} {playerName}
                </div>
                <div className="btn btn-danger" onClick={backToPool}>
                    Back to wait pool
                </div>
                <div>
                    {getGameCompleteMessage()}
                </div>
            </div>
        </div>);
}

export default GameBoard;
