import React, {useContext, useEffect, useState, useCallback, useRef} from 'react';
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
    const [gameActive] = useState(true);
    const [winningPlayer, setWinningPlayer] = useState(null);
    const {stompClient} = useContext(StompContext);
    const { pageId } = useParams();
    const navigate = useNavigate();
    const subscriptionRef = useRef(null);

    const backToPool = () => {
        websocket.register(stompClient, playerName);
        const payload = { state: { userName: playerName } };
        navigate('/pool', payload);
    }

    const parseGameDetails = useCallback((details) => {
        const { playerName, playerId, opponentName, opponentId, board, winningPlayer } = details;
        const {tickSpaces, crossPlayerId, activePlayerId} = board;
        setPlayerName(playerName);
        setPlayerId(playerId);
        setOpponentName(opponentName);
        setOpponentId(opponentId);
        setTickSpaces(tickSpaces);
        setActivePlayerId(activePlayerId);
        setCrossPlayerId(crossPlayerId);
        setWinningPlayer(winningPlayer);
    }, []);

    const isCrossTick = useCallback(() => {
        return playerId === crossPlayerId;
    }, [playerId, crossPlayerId]);

    const sendNextStep = useCallback((updatedTickSpaces) => {
        const stepPayload = {
            "playerName": playerName,
            "playerId": playerId,
            "opponentName": opponentName,
            "opponentId": opponentId,
            "board": {
                "roomId": pageId,
                "tickSpaces": updatedTickSpaces,
                "crossPlayerId": crossPlayerId,
                "activePlayerId": activePlayerId
            }
        };
        stompClient.publish({
            destination: '/app/step',
            body: JSON.stringify(stepPayload)
        });
    }, [playerName, playerId, opponentName, opponentId, pageId, crossPlayerId, activePlayerId, stompClient]);

    const handleClick = useCallback((tickSpace) => {
        if(activePlayerId === playerId && gameActive && !winningPlayer) {
            const index = tickSpaces.findIndex(t => t.id === tickSpace.id);
            if (index !== -1 && !tickSpaces[index].clicked) {
                // Create new array with updated tick space (avoid mutation)
                const updatedTickSpaces = tickSpaces.map((space, i) =>
                    i === index
                        ? { ...space, clicked: true, crossed: isCrossTick() }
                        : space
                );
                setTickSpaces(updatedTickSpaces);
                sendNextStep(updatedTickSpaces);
            }
        }
    }, [activePlayerId, playerId, gameActive, winningPlayer, tickSpaces, isCrossTick, sendNextStep]);

    const getTickSpaceClass = useCallback(() => {
        return activePlayerId === playerId ? "active-player" : "inactive-player";
    }, [activePlayerId, playerId]);

    // Parse initial game details
    useEffect(() => {
        if (gameDetails) {
            parseGameDetails(gameDetails);
        }
    }, [gameDetails, parseGameDetails]);

    // Subscribe to game updates with proper cleanup
    useEffect(() => {
        if (stompClient) {
            const gameStepHandler = (message) => {
                const response = JSON.parse(message.body);
                parseGameDetails(response);
            };

            subscriptionRef.current = stompClient.subscribe(
                process.env.REACT_APP_SUBSCRIPTION_TOPIC,
                gameStepHandler
            );

            return () => {
                if (subscriptionRef.current) {
                    subscriptionRef.current.unsubscribe();
                }
            };
        }
    }, [stompClient, parseGameDetails]);

    const getGameCompleteMessage = () => {
        if(winningPlayer) {
            if(winningPlayer === playerId)
                return <p>Congrats {playerName} you won!!!</p>
            else
                return <p>Better Luck Next Time</p>
        }
        return null;
    };

    const isCrossPlayer = () => {
        return playerId === crossPlayerId;
    };

    const getNameStyle = () => {
        const styles = {textAlign: "right"};
        if(playerId === activePlayerId) {
            return {...styles, fontSize: "25", fontWeight: "bold"};
        }
        return styles;
    };

    const getGamePlayMessage = () => {
        return playerId === activePlayerId ? "your turn " : "";
    };

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
