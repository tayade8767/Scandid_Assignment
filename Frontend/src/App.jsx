/* eslint-disable no-unused-vars */


import React from 'react'
import { Routes,Route } from 'react-router-dom';
import TransactionDashboard from './TransactionDashboard';

function App() {
  return (
    <Routes>
      <Route path='/' element={<TransactionDashboard/>} />
    </Routes>
  )
}

export default App