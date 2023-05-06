import React, {useEffect, useState} from 'react';
import Form from "./Form";
import Joi from "joi-browser";
import Cookies from 'js-cookie';

function WelcomePageForm({setUserName, initiateWebSocket}) {

    const [formErrors, setFormErrors] = useState({})
    const [userDetails, setUserDetails] = useState({userName: ""});

    useEffect(() => {
        const myCookieValue = Cookies.get('userName');
        if(myCookieValue) {
            setUserDetails({userName: myCookieValue});
        }
        return () => {};
    }, []);

    const formSchema = {
        userName: Joi.string().min(3).required().label('Name')
    }

    const validateProperty = ({name, value}) => {
        const obj = { [name]: value};
        const schema = {[name]: formSchema[name]}
        const {error} = Joi.validate(obj, schema);
        return error ?  error.details[0].message : null;
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
        const {error} = Joi.validate(userDetails, formSchema, options);
        if(!error)  return null;
        const errors = {};
        for(let item of error.details)
            errors[item.path[0]] = item.message;
        return errors;
    }
    const showErrors = (errors) => {
        setFormErrors(errors ? errors : {});
    }

    const submitToServer = () => {
        Cookies.set('userName', userDetails.userName);
        setUserName(userDetails.userName);
        initiateWebSocket(userDetails.userName);
    }

    return (
        <div className="row">
            <div className="col-md-8 offset-md-3 col-sm-12">
                <Form
                    userName={userDetails.userName}
                    handleChange={handleChange}
                    errors={formErrors}
                    formValidator={validate}
                    showErrors={showErrors}
                    submitToServer={submitToServer}
                />
            </div>
        </div>
    );
}

export default WelcomePageForm;