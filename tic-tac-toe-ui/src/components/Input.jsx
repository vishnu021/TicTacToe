import React from "react";

const Input = ({name, label, value, onChange, placeholder, helpText, helpTextId }) => {
    return (
        <div className="form-group">
            <label htmlFor={name} className="m-1">
                {label}
            </label>
            <input
                type="text"
                className="form-control"
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                error="Error Message"
                aria-describedby={helpTextId}
                placeholder={placeholder} />
            <small id={helpTextId} className="form-text text-muted" style={{fontSize: 10}}>{helpText}</small>
        </div>
    );
};

export default Input;