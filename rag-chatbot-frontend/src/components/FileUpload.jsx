import React from 'react';

const FileUpload = ({ onUpload }) => {
    const handleFileChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            onUpload(file);
        }
    };

    return (
        <div className="file-upload">
            <input type="file" onChange={handleFileChange} />
        </div>
    );
};

export default FileUpload;