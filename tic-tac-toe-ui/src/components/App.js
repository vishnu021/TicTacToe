import '../styles/App.css';
import 'bootstrap/dist/css/bootstrap.css'
import NotFound from "./notFound";
import {Navigate, Route, Routes} from 'react-router-dom';
import React from 'react';
import ParentComponent from "./parentComponent";
function App() {
    return(
        <div className="bg-light" style={{backgroundColor: "lightgrey"}}>
            <div className="content">
                <Routes>
                    <Route path="/" element={<ParentComponent />}/>
                    <Route path="/play/:pageId" element={<ParentComponent />}/>
                    <Route path='/not-found' element={<NotFound/>} />
                    <Route path="*" element={<Navigate to ="/not-found" />}/>
                </Routes>
            </div>
        </div>
  );
}

export default App;