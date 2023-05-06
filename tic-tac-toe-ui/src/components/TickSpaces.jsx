import React from 'react';
import TickSpace from "./TickSpace";

function TickSpaces({tickSpaces, handleClick, isCrossPlayer}) {

    const getTickSpaces = () => {
        const entries = tickSpaces.map(tickSpace =>{
            return  <td
                key={tickSpace.id}
                style={{ borderColor: 'black', borderWidth: '3px', textAlign: 'center', verticalAlign: 'middle'}}
                onClick={() => handleClick(tickSpace)}
            >
                <TickSpace
                    style={{ background: 'transparent' }}
                    key={tickSpace.id}
                    tickSpace={tickSpace}
                    isCrossPlayer={isCrossPlayer}
                />
            </td>;
        });

        const row = entries.reduce((acc, curr, i) => {
            if(i%3===0) {
                acc.push([])
            }
            acc[acc.length-1].push(curr);
            return acc;
        }, []);

        return row.map((group, i) => (
            <tr key={i}>
                {group}
            </tr>
        ));
    }

    return (
        <table className="table">
            <tbody>
            {getTickSpaces(tickSpaces)}
            </tbody>
        </table>
    );
}

export default TickSpaces;