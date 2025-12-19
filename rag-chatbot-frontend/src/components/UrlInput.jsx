import React, { useState } from 'react';

const UrlInput = ({ onSubmit }) => {
    const [url, setUrl] = useState('');

    const handleSubmit = () => {
        if (url.trim()) {
            onSubmit(url);
            setUrl('');
        }
    };

    return (
        <div className="url-input">
            <input
                type="text"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
                placeholder="Enter a web page URL..."
            />
            <button onClick={handleSubmit}>Submit</button>
        </div>
    );
};

export default UrlInput;