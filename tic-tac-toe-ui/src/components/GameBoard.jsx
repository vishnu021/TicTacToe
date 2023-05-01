import React, {useEffect, useState} from 'react';
import websocket from '../services/webSocketService'
import TickSpaces from "./TickSpaces";
import CelebrationAnimation from "./CelebrationAnimation";

function GameBoard({stompClient, gameDetails}) {
    const [playerName, setPlayerName] = useState("");
    const [playerId, setPlayerId] = useState("");
    const [opponentName, setOpponentName] = useState("");
    const [tickSpaces, setTickSpaces] = useState([]);
    const [crossPlayerId, setCrossPlayerId] = useState("");
    const [activePlayerId, setActivePlayerId] = useState("");
    const [opponentId, setOpponentId] = useState("");
    const [gameActive, setGameActive] = useState(true);
    const [winningPlayer, setWinningPlayer] = useState(null);

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
        return playerId===crossPlayerId;
    }

    const sendNextStep = () => {
        const stepPayload = {
            "playerName": playerName,
            "playerId": playerId,
            "opponentName": opponentName,
            "opponentId": opponentId,
            "board": {
                "roomId": 0,
                "tickSpaces": tickSpaces,
                "crossPlayerId": crossPlayerId,
                "activePlayerId": activePlayerId
            }
        }
        console.log("sending next step", stepPayload);
        stompClient.send('/app/step', {}, JSON.stringify(stepPayload));
    }

    const handleClick = tickSpace =>  {
        if(activePlayerId===playerId && gameActive && !winningPlayer) {
            const boardTickSpaces = tickSpaces
            const index = boardTickSpaces.indexOf(tickSpace);
            if (!boardTickSpaces[index].clicked) {
                boardTickSpaces[index].clicked = true;
                boardTickSpaces[index].crossed = isCrossTick();
                sendNextStep();
            }
            console.log("updating tickspaces in handleClick to ", boardTickSpaces);
            setTickSpaces(boardTickSpaces)
        }
    }

    const getTickSpaceStyle = () => {
        console.log(`playerId : ${playerId}, active player : ${activePlayerId}`);
        if(activePlayerId===playerId) {
            return {cursor: 'pointer', borderColor: '#6c757d', borderWidth: '3px'};
        } else {
            return {borderColor: '#6c757d', borderWidth: '3px'};
        }
    }

    useEffect(() => {
        parseGameDetails(gameDetails);
        const connectToWebSocket = async () => {
            return () => {
                stompClient.disconnect();
            };
        };

        connectToWebSocket();
    }, []);

    const gameStepHandler = (message) => {
        const response = JSON.parse(message.body);
        console.log('step response:', response);
        parseGameDetails(response);
    }

    const getGameCompleteMessage = () => {
        if(winningPlayer) {
            if(winningPlayer===playerId)
                return <p>Congrats {playerName} you won!!!</p>
            else
                return <p>Better Luck Next Time</p>

        }
        return <p></p>;
    }
    websocket.subscribeToGame(stompClient, gameStepHandler);
    return (
        <div className="row m-2 game-font">
            <div className="col-md-4 offset-md-4 col-sm-12">
                <CelebrationAnimation
                    animationStarted={winningPlayer===playerId}
                    duration={5000}
                    numberOfParticles={500} />
                <h2 className="m-3">TIC TAC TOE</h2>
                <div style={{textAlign: "left", color: "red"}} className="m-2">
                    {opponentName}
                </div>
                <TickSpaces
                    tickSpaces={tickSpaces}
                    getTickSpaceStyle={getTickSpaceStyle}
                    handleClick = {handleClick}
                />
                <div style={{textAlign: "right", color: "blue"}} className="m-2">
                    {playerName}
                </div>
                <div>
                    {getGameCompleteMessage()}
                </div>
            </div>
        </div>);
}

export default GameBoard;
