import '../styles/App.css';
import 'bootstrap/dist/css/bootstrap.css'
import NotFound from "./notFound";
import {Navigate, Route, Routes} from 'react-router-dom';
import React from 'react';
import ParentComponent from "./parentComponent";
import WelcomePageForm from "./welcomePageForm";
import WaitingPool from "./waitingPool";
import Parent from "./parent";


function App() {
    return(
        <div className="content">
            <Parent>
                <Routes>
                    <Route path="/" element={<WelcomePageForm />}/>
                    <Route path="/pool" element={<WaitingPool />}/>
                    <Route path="/play/:pageId" element={<ParentComponent />}/>
                    <Route path='/not-found' element={<NotFound/>} />
                    <Route path="*" element={<Navigate to ="/not-found" />}/>
                </Routes>
            </Parent>
        </div>
  );
}

export default App;