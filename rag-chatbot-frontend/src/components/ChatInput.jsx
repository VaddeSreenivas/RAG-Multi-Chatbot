import React, { useState } from 'react';

const ChatInput = ({ onSend }) => {
    const [question, setQuestion] = useState('');

    const handleSend = () => {
        if (question.trim()) {
            onSend(question);
            setQuestion('');
        }
    };

    return (
        <div className="chat-input">
            <input
                type="text"
                value={question}
                onChange={(e) => setQuestion(e.target.value)}
                placeholder="Type your question here..."
            />
            <button onClick={handleSend}>Send</button>
        </div>
    );
};

export default ChatInput;