import React from 'react';
import Input from "./Input";
import Checkbox from "./Checkbox";

function Form({ userName, handleChange, handleCheckChange, errors, formValidator, showErrors,
                  checkboxStatus, submitToServer}) {

    const handleSubmit = async (event) => {
        event.preventDefault(); //prevent the default behaviour of the form to submit to server
        const errors = formValidator();
        if (errors) {
            showErrors(errors);
            return;
        }
        submitToServer();
    };

    return (
        <form onSubmit={handleSubmit}
              className="game-font"
              style={{fontSize: 20}}>
            <table style={{tableLayout: "fixed", width: "50%"}}>
                <tbody>
                <tr>
                    <td style={{textAlign: "center"}}>
                        <p className="m-2 game-font text-black p-4" style={{fontSize: 30}}>
                            Welcome to tic tac toe
                        </p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <Input
                            name="userName"
                            label="Please enter your name : "
                            value={userName}
                            onChange={handleChange}
                            placeholder="your name here"
                            error={errors.error}
                            helpText="Likh bhi do"
                            helpTextId="nameHelp" />
                        {errors.userName && (
                            <div style={{ color: 'red',fontSize: 15 }}>
                                {errors.userName}
                            </div>
                        )}
                    </td>
                </tr>
                <tr>
                    <td>
                        <Checkbox value={checkboxStatus} handleCheckChange={handleCheckChange}/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input
                            disabled={formValidator()}
                            type="submit"
                            value="Play"
                            className="btn btn-primary m-2"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    );
}
export default Form;
