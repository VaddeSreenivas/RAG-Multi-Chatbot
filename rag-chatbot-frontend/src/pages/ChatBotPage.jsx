import React, { useState, useRef } from 'react';
import {
    sendQuestion,
    uploadImage,
    generateImage,
    uploadAudio,
    generateAudio,
    uploadUrl,
    askUrlData,
    uploadPdf,
    askPdfData,
} from '../services/api';
import '../App.css';
import { FaCommentDots, FaImage, FaFilePdf, FaMicrophone, FaGlobe } from 'react-icons/fa';

const ChatBotPage = () => {
    const [activeTab, setActiveTab] = useState('Chat');

    // Chat
    const [question, setQuestion] = useState('');
    const [response, setResponse] = useState('');
    const [loadingChat, setLoadingChat] = useState(false);

    // Image
    const [imageFile, setImageFile] = useState(null);
    const [imageUploaded, setImageUploaded] = useState(false);
    const [imageAnswer, setImageAnswer] = useState('');
    const [imagePrompt, setImagePrompt] = useState('');
    const [generatedImageUrl, setGeneratedImageUrl] = useState('');
    const [loadingImageUpload, setLoadingImageUpload] = useState(false);
    const [loadingImageGenerate, setLoadingImageGenerate] = useState(false);

    // PDF
    const [pdfFile, setPdfFile] = useState(null);
    const [pdfUploaded, setPdfUploaded] = useState(false);
    const [pdfQuestion, setPdfQuestion] = useState('');
    const [pdfAnswer, setPdfAnswer] = useState('');
    const [pdfUploadMessage, setPdfUploadMessage] = useState('');
    const [loadingPdfUpload, setLoadingPdfUpload] = useState(false);
    const [loadingPdfAsk, setLoadingPdfAsk] = useState(false);

    // Audio
    const [audioFile, setAudioFile] = useState(null);
    const [audioUploaded, setAudioUploaded] = useState(false);
    const [audioTranscribedText, setAudioTranscribedText] = useState('');
    const [audioTextToGenerate, setAudioTextToGenerate] = useState('');
    const [audioBlob, setAudioBlob] = useState(null);
    const [loadingAudioUpload, setLoadingAudioUpload] = useState(false);
    const [loadingAudioGenerate, setLoadingAudioGenerate] = useState(false);
    const audioRef = useRef();

    // URL
    const [url, setUrl] = useState('');
    const [urlQuestion, setUrlQuestion] = useState('');
    const [urlAnswer, setUrlAnswer] = useState('');
    const [urlUploadMessage, setUrlUploadMessage] = useState('');
    const [urlUploadSuccess, setUrlUploadSuccess] = useState(null);
    const [loadingUrlUpload, setLoadingUrlUpload] = useState(false);
    const [loadingUrlAsk, setLoadingUrlAsk] = useState(false);

    // Common styles
    const inputStyle = {
        flex: 1,
        padding: 12,
        borderRadius: 8,
        border: '1px solid #aaa',
        fontSize: 16,
    };

    const buttonStyle = {
        height: 40,
        padding: '0 16px',
        background: '#4a3fb0;',
        color: '#fff',
        borderRadius: 10,
        // border: '2px solid #22004d',
        cursor: 'pointer',
    };

    const responseStyle = {
        background: '#fff',
        border: '1px solid #ddd',
        borderRadius: '8px',
        padding: '16px',
        fontSize: '16px',
        lineHeight: '1.6',
        marginTop: '16px',
        whiteSpace: 'pre-wrap',
    };

    // Spinner styles (Option B)
    const spinnerContainer = {
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        justifyContent: 'center',
        padding: 12,
    };

    const spinner = {
        width: 20,
        height: 20,
        border: '3px solid rgba(0,0,0,0.12)',
        borderTop: '3px solid rgba(0,0,0,0.6)',
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
    };

    // include keyframes via a <style> element so copy/paste works
    const InlineSpinnerKeyframes = () => (
        <style>
            {`@keyframes spin { from { transform: rotate(0deg);} to { transform: rotate(360deg);} }`}
        </style>
    );

    // ---------------- Handlers ----------------

    // Chat
    const handleSendQuestion = async () => {
        if (!question.trim()) return;
        setLoadingChat(true);
        setResponse('');
        try {
            const res = await sendQuestion(question);
            setResponse(res);
        } catch (err) {
            console.error(err);
            setResponse('Error: failed to get response.');
        } finally {
            setLoadingChat(false);
        }
    };

    // Image
    const handleImageFileChange = (e) => {
        setImageFile(e.target.files[0]);
        setImageUploaded(false);
        setImageAnswer('');
    };

    const handleImageUpload = async () => {
        if (!imageFile) return;
        setLoadingImageUpload(true);
        setImageUploaded(false);
        setImageAnswer('');
        try {
            const res = await uploadImage(imageFile);
            setImageUploaded(true);
            setImageAnswer(res);
        } catch (err) {
            console.error(err);
            setImageAnswer('Error uploading image.');
        } finally {
            setLoadingImageUpload(false);
        }
    };

    const handleGenerateImage = async () => {
        if (!imagePrompt.trim()) return;
        setLoadingImageGenerate(true);
        setGeneratedImageUrl('');
        try {
            const res = await generateImage(imagePrompt);
            if (typeof res === 'string' && res.startsWith('http')) {
                setGeneratedImageUrl(res);
            } else {
                setGeneratedImageUrl('');
                setResponse(res);
            }
        } catch (err) {
            console.error(err);
            setResponse('Error generating image.');
        } finally {
            setLoadingImageGenerate(false);
        }
    };

    // PDF
    const handlePdfFileChange = (e) => {
        setPdfFile(e.target.files[0]);
        setPdfUploaded(false);
        setPdfQuestion('');
        setPdfAnswer('');
        setPdfUploadMessage('');
    };

    const handlePdfUpload = async () => {
        if (!pdfFile) {
            setPdfUploadMessage('Please select a PDF first.');
            return;
        }
        setLoadingPdfUpload(true);
        setPdfUploadMessage('');
        try {
            await uploadPdf(pdfFile);
            setPdfUploadMessage('PDF ingested successfully!');
            setPdfUploaded(true);
        } catch (err) {
            console.error(err);
            setPdfUploadMessage('Upload failed. Please try again.');
        } finally {
            setLoadingPdfUpload(false);
        }
    };

    const handleAskPdfData = async () => {
        if (!pdfQuestion.trim()) return;
        setLoadingPdfAsk(true);
        setPdfAnswer('');
        try {
            const res = await askPdfData(pdfQuestion);
            setPdfAnswer(res);
        } catch (err) {
            console.error(err);
            setPdfAnswer('Error fetching PDF data.');
        } finally {
            setLoadingPdfAsk(false);
        }
    };

    // Audio
    const handleAudioFileChange = (e) => {
        setAudioFile(e.target.files[0]);
        setAudioUploaded(false);
        setAudioTranscribedText('');
    };

    const handleAudioUpload = async () => {
        if (!audioFile) return;
        setLoadingAudioUpload(true);
        setAudioUploaded(false);
        setAudioTranscribedText('');
        try {
            const res = await uploadAudio(audioFile);
            setAudioUploaded(true);
            setAudioTranscribedText(res);
        } catch (err) {
            console.error(err);
            setAudioTranscribedText('Error uploading audio.');
        } finally {
            setLoadingAudioUpload(false);
        }
    };

    const handleGenerateAudio = async () => {
        if (!audioTextToGenerate.trim()) return;
        setLoadingAudioGenerate(true);
        setAudioBlob(null);
        try {
            const blob = await generateAudio(audioTextToGenerate);
            setAudioBlob(blob);
            if (audioRef.current) audioRef.current.load();
        } catch (err) {
            console.error(err);
        } finally {
            setLoadingAudioGenerate(false);
        }
    };

    // URL
    const handleUrlUpload = async () => {
        if (!url.trim()) {
            setUrlUploadSuccess(false);
            setUrlUploadMessage('Please enter a URL.');
            return;
        }
        setLoadingUrlUpload(true);
        setUrlUploadMessage('');
        setUrlUploadSuccess(null);
        try {
            await uploadUrl(url);
            setUrlUploadSuccess(true);
            setUrlUploadMessage('URL uploaded successfully!');
            setUrlAnswer('');
        } catch (err) {
            console.error(err);
            setUrlUploadSuccess(false);
            setUrlUploadMessage('Upload failed. Please try again.');
        } finally {
            setLoadingUrlUpload(false);
        }
    };

    const handleAskUrlData = async () => {
        if (!urlQuestion.trim()) return;
        setLoadingUrlAsk(true);
        setUrlAnswer('');
        try {
            const res = await askUrlData(urlQuestion);
            setUrlAnswer(res);
        } catch (err) {
            console.error(err);
            setUrlAnswer('Error fetching URL data.');
        } finally {
            setLoadingUrlAsk(false);
        }
    };

    // Render
    const renderContent = () => {
        switch (activeTab) {
            case 'Chat':
                return (
                    <div className="content">
                        <InlineSpinnerKeyframes />
                        <h2>Chat</h2>

                        <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
                            <input
                                type="text"
                                placeholder="Type your question..."
                                value={question}
                                onChange={(e) => setQuestion(e.target.value)}
                                style={inputStyle}
                            />
                            <button onClick={handleSendQuestion} className="button" style={buttonStyle} disabled={loadingChat}>
                                Send
                            </button>
                        </div>

                        {loadingChat ? (
                            <div style={{ ...responseStyle, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <div style={spinnerContainer}>
                                    <div style={spinner} />
                                    {/* <div>Thinking…</div> */}
                                </div>
                            </div>
                        ) : (
                            <div
                                style={{
                                    ...responseStyle,
                                    position: 'relative',
                                    color: response ? '#000' : '#999',
                                    maxHeight: '350px',
                                    overflowY: 'auto',
                                    paddingRight: '10px',
                                }}
                            >
                                {response ? response : 'Chatbot response will appear here'}
                            </div>
                        )}
                    </div>
                );

            case 'Image':
                return (
                    <div className="content">
                        <InlineSpinnerKeyframes />
                        <h2>Image</h2>

                        {/* Upload Image */}
                        <div style={{ marginBottom: 16 }}>
                            <label>Upload an image</label>
                            <div style={{ display: 'flex', gap: 8 }}>
                                <input type="file" accept="image/*" onChange={handleImageFileChange} style={inputStyle} />
                                <button onClick={handleImageUpload} className="button" style={buttonStyle} disabled={loadingImageUpload}>
                                    Describe Image
                                </button>
                            </div>

                            {loadingImageUpload ? (
                                <div style={{ ...responseStyle, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                    <div style={spinnerContainer}>
                                        <div style={spinner} />
                                        <div>Analyzing image…</div>
                                    </div>
                                </div>
                            ) : (
                                imageUploaded && (
                                    <div
                                        style={{
                                            ...responseStyle,
                                            maxHeight: '200px',
                                            overflowY: 'auto',
                                            paddingRight: '8px',
                                        }}
                                    >
                                        {imageAnswer}
                                    </div>
                                )
                            )}
                        </div>

                        {/* Generate Image */}
                        <div>
                            <label>Generate image from description</label>
                            <div style={{ display: 'flex', gap: 8 }}>
                                <input
                                    type="text"
                                    placeholder="Type image description..."
                                    value={imagePrompt}
                                    onChange={(e) => setImagePrompt(e.target.value)}
                                    style={inputStyle}
                                />
                                <button onClick={handleGenerateImage} className="button" style={buttonStyle} disabled={loadingImageGenerate}>
                                    Generate Image
                                </button>
                            </div>

                            {loadingImageGenerate ? (
                                <div style={{ ...responseStyle, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                    <div style={spinnerContainer}>
                                        <div style={spinner} />
                                        <div>Generating image…</div>
                                    </div>
                                </div>
                            ) : (
                                generatedImageUrl && (
                                    <div
                                        style={{
                                            ...responseStyle,
                                            display: "flex",
                                            justifyContent: "center",
                                            alignItems: "center",
                                            padding: "0",              // remove extra padding
                                            borderRadius: "10px",
                                            overflow: "hidden",        // prevents scroll bars & removes extra white-space
                                            maxHeight: "400px",        // limit the box height
                                            width: "100%",             // make container width flexible
                                        }}
                                    >
                                        <img
                                            src={generatedImageUrl}
                                            alt="Generated"
                                            style={{
                                                maxWidth: "100%",       // image can't be wider than container
                                                maxHeight: "400px",     // image can't be taller than container max height
                                                objectFit: "contain",   // maintain aspect ratio & fit inside
                                                borderRadius: "10px",
                                                display: "block",
                                            }}
                                        />
                                    </div>



                                )
                            )}
                        </div>
                    </div>
                );

            case 'PDF':
                return (
                    <div className="content">
                        <InlineSpinnerKeyframes />
                        <h2>PDF</h2>

                        <div style={{ display: 'flex', gap: 8 }}>
                            <input type="file" accept="application/pdf" onChange={handlePdfFileChange} style={inputStyle} />
                            <button onClick={handlePdfUpload} className="button" style={buttonStyle} disabled={loadingPdfUpload}>
                                Submit PDF
                            </button>
                            {pdfUploadMessage && (
                                <span style={{ color: pdfUploadMessage.includes('success') ? 'green' : 'red' }}>{pdfUploadMessage}</span>
                            )}
                        </div>

                        <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                            <input
                                type="text"
                                placeholder="Ask about the PDF..."
                                value={pdfQuestion}
                                onChange={(e) => setPdfQuestion(e.target.value)}
                                style={inputStyle}
                            />
                            <button onClick={handleAskPdfData} className="button" style={buttonStyle} disabled={loadingPdfAsk}>
                                Ask from PDF Data
                            </button>
                        </div>

                        {loadingPdfAsk ? (
                            <div style={{ ...responseStyle, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                <div style={spinnerContainer}>
                                    <div style={spinner} />
                                    <div>Fetching answer from PDF…</div>
                                </div>
                            </div>
                        ) : (
                            pdfAnswer && (
                                <div
                                    style={{
                                        ...responseStyle,
                                        maxHeight: '350px',
                                        overflowY: 'auto',
                                        paddingRight: '10px',
                                    }}
                                >
                                    {pdfAnswer}
                                </div>
                            )
                        )}
                    </div>
                );

            case 'Audio':
                return (
                    <div className="content">
                        <InlineSpinnerKeyframes />
                        <h2>Audio</h2>

                        <div style={{ display: 'flex', gap: 8 }}>
                            <input type="file" accept="audio/*" onChange={handleAudioFileChange} style={inputStyle} />
                            <button onClick={handleAudioUpload} className="button" style={buttonStyle} disabled={loadingAudioUpload}>
                                Generate Text
                            </button>
                        </div>

                        {loadingAudioUpload ? (
                            <div style={{ ...responseStyle, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                <div style={spinnerContainer}>
                                    <div style={spinner} />
                                    <div>Transcribing audio…</div>
                                </div>
                            </div>
                        ) : (
                            audioUploaded && (
                                <div style={{ ...responseStyle, maxHeight: '300px', overflowY: 'auto', paddingRight: '10px' }}>
                                    <strong>Transcribed text:</strong> {audioTranscribedText}
                                </div>
                            )
                        )}

                        <div style={{ display: 'flex', gap: 8, marginTop: 16 }}>
                            <input
                                type="text"
                                placeholder="Type text to generate audio..."
                                value={audioTextToGenerate}
                                onChange={(e) => setAudioTextToGenerate(e.target.value)}
                                style={inputStyle}
                            />
                            <button onClick={handleGenerateAudio} className="button" style={buttonStyle} disabled={loadingAudioGenerate}>
                                Generate Audio
                            </button>
                        </div>

                        {loadingAudioGenerate ? (
                            <div style={{ ...responseStyle, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                <div style={spinnerContainer}>
                                    <div style={spinner} />
                                    <div>Generating audio…</div>
                                </div>
                            </div>
                        ) : (
                            audioBlob && (
                                <audio ref={audioRef} controls style={{ marginTop: 8, width: '100%' }}>
                                    <source src={URL.createObjectURL(audioBlob)} type="audio/mp3" />
                                </audio>
                            )
                        )}
                    </div>
                );

            case 'Web URL':
                return (
                    <div className="content">
                        <InlineSpinnerKeyframes />
                        <h2>Web URL</h2>

                        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                            <input type="text" placeholder="Enter web page URL..." value={url} onChange={(e) => setUrl(e.target.value)} style={inputStyle} />
                            <button onClick={handleUrlUpload} className="button" style={buttonStyle} disabled={loadingUrlUpload}>
                                Upload URL
                            </button>

                            {urlUploadMessage && (
                                <span style={{ color: urlUploadSuccess ? 'green' : 'red', marginLeft: 8 }}>{urlUploadMessage}</span>
                            )}
                        </div>

                        <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                            <input type="text" placeholder="Ask about the URL..." value={urlQuestion} onChange={(e) => setUrlQuestion(e.target.value)} style={inputStyle} />
                            <button onClick={handleAskUrlData} className="button" style={buttonStyle} disabled={loadingUrlAsk}>
                                Ask from URL Data
                            </button>
                        </div>

                        {loadingUrlAsk ? (
                            <div style={{ ...responseStyle, display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                                <div style={spinnerContainer}>
                                    <div style={spinner} />
                                    <div>Querying URL data…</div>
                                </div>
                            </div>
                        ) : (
                            urlAnswer && (
                                <div style={{ ...responseStyle, maxHeight: '350px', overflowY: 'auto', paddingRight: '10px' }}>{urlAnswer}</div>
                            )
                        )}
                    </div>
                );

            default:
                return null;
        }
    };

    return (
        <div className="chatbot-page">
            <div className="sidebar">
                <h3>RAG Multi-Chatbot</h3>
                <ul>
                    {['Chat', 'Image', 'PDF', 'Audio', 'Web URL'].map((tab) => (
                        <li
                            key={tab}
                            onClick={() => setActiveTab(tab)}
                            className={activeTab === tab ? 'active' : ''}
                            style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8, padding: '8px 12px' }}
                        >
                            <span className="icon">
                                {tab === 'Chat' && <FaCommentDots />}
                                {tab === 'Image' && <FaImage />}
                                {tab === 'PDF' && <FaFilePdf />}
                                {tab === 'Audio' && <FaMicrophone />}
                                {tab === 'Web URL' && <FaGlobe />}
                            </span>
                            {tab}
                        </li>
                    ))}
                </ul>
            </div>

            <div className="main-content">{renderContent()}</div>
        </div>
    );
};

export default ChatBotPage;
