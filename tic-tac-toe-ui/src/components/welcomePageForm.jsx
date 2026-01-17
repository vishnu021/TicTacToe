import React, {useContext, useEffect, useState} from 'react';
import Form from "./Form";
import Joi from "joi";
import Cookies from 'js-cookie';
import websocket from "../services/webSocketService";
import { useNavigate } from 'react-router-dom';
import {StompContext} from "./parent";

function WelcomePageForm() {

    const [formErrors, setFormErrors] = useState({})
    const [userDetails, setUserDetails] = useState({userName: ""});
    const { stompClient } = useContext(StompContext);
    const navigate = useNavigate();

    const registerAndNavigate = (userName) => {
        websocket.register(stompClient, userName);
        const payload = { state: { userName: userName } };
        navigate('/pool', payload);
    };

    const connectToWebSocket = async (userName) => {
        registerAndNavigate(userName);
    };

    useEffect(() => {
        const myCookieValue = Cookies.get('userName');
        if(myCookieValue) {
            setUserDetails({userName: myCookieValue});
        }
    }, []);

    const formSchema = Joi.object({
        userName: Joi.string().min(3).required().label('Name')
    });

    const validateProperty = ({name, value}) => {
        const obj = { [name]: value};
        const schema = Joi.object({[name]: formSchema.extract(name)});
        const {error} = schema.validate(obj);
        return error ? error.details[0].message : null;
    }

    const handleChange = ({currentTarget:input}) => {
        const errors = {...formErrors};
        const errorMessage = validateProperty(input);
        if(errorMessage)
            errors[input.name] = errorMessage;
        else
            delete errors[input.name];

        const userData = {...userDetails}
        userData[input.name] = input.value;

        setFormErrors(errors);
        setUserDetails(userData);
    }

    const validate = () => {
        const options = { abortEarly: true };
        const {error} = formSchema.validate(userDetails, options);
        if(!error) return null;
        const errors = {};
        for(let item of error.details)
            errors[item.path[0]] = item.message;
        return errors;
    }

    const showErrors = (errors) => {
        setFormErrors(errors ? errors : {});
    }

    const submitToServer = async () => {
        Cookies.set('userName', userDetails.userName);
        await connectToWebSocket(userDetails.userName);
    }

    return (
        <div className="col-md-4 col-sm-12">
            <Form
                userName={userDetails.userName}
                handleChange={handleChange}
                errors={formErrors}
                formValidator={validate}
                showErrors={showErrors}
                submitToServer={submitToServer}
            />
        </div>
    );
}

export default WelcomePageForm;
