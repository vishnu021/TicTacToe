import React from 'react';

function Checkbox({value, handleCheckChange}) {
    return (
        <React.Fragment>
            <div className="form-check  m-2" style={{fontSize: 15}}>
                <input className="form-check-input"
                       type="checkbox"
                       id="flexCheckDefault"
                       onChange={handleCheckChange}
                       checked={value}
                />
                    <label className="form-check-label" htmlFor="flexCheckDefault" style={{cursor: 'pointer'}}>
                        create new room
                    </label>
            </div>
        </React.Fragment>
    );
}

export default Checkbox;