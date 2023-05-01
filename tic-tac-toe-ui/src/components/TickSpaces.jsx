import React from 'react';
import TickSpace from "./TickSpace";

function TickSpaces({tickSpaces, getTickSpaceStyle, handleClick}) {


    const getTickSpaces = () => {
        console.log("tickSpaces : ", tickSpaces);

        const entries = tickSpaces.map(tickSpace =>{
            return  <td
                key={tickSpace.id}
                style={getTickSpaceStyle()}
                onClick={() => handleClick(tickSpace)}
            >
                <TickSpace
                    style={{ width: '100%', height: '100%', padding: 0, border: 'none', background: 'transparent' }}
                    key={tickSpace.id}
                    tickSpace={tickSpace}
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