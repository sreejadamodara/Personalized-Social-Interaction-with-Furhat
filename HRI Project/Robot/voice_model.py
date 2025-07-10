import pyaudio
import wave
import numpy as np
from pydub import AudioSegment
from python_speech_features import mfcc
from sklearn import svm
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
import speech_recognition as sr
import os


# 1. Record Audio in Real-Time and Save as a WAV File
def record_audio(file_path, duration=5, sample_rate=44100, chunk_size=1024, channels=1):
    audio_format = pyaudio.paInt16  # 16-bit resolution
    p = pyaudio.PyAudio()  # Create an audio object

    print("Recording...")

    # Open a stream for recording
    stream = p.open(format=audio_format,
                    channels=channels,
                    rate=sample_rate,
                    input=True,
                    frames_per_buffer=chunk_size)

    frames = []

    # Capture audio in chunks
    for _ in range(0, int(sample_rate / chunk_size * duration)):
        data = stream.read(chunk_size)
        frames.append(data)

    print("Recording complete.")

    # Stop and close the stream
    stream.stop_stream()
    stream.close()
    p.terminate()

    # Save the recorded audio to a .wav file
    wf = wave.open(file_path, 'wb')
    wf.setnchannels(channels)
    wf.setsampwidth(p.get_sample_size(audio_format))
    wf.setframerate(sample_rate)
    wf.writeframes(b''.join(frames))
    wf.close()


# 2. Extract MFCC features for Speaker Identification
def extract_mfcc_pydub(file_path):
    # Load the audio file
    audio = AudioSegment.from_wav(file_path)  # Load as a .wav file

    # Convert to raw audio data (array of samples)
    samples = np.array(audio.get_array_of_samples())

    # Extract MFCC features
    mfcc_features = mfcc(samples, samplerate=audio.frame_rate, numcep=13)
    return np.mean(mfcc_features, axis=0)


# 3. Train Speaker Identification Model (using saved audio files for training)
def train_speaker_model(data_dir):
    X = []  # Store MFCC features
    y = []  # Store speaker labels

    for speaker_dir in os.listdir(data_dir):
        speaker_path = os.path.join(data_dir, speaker_dir)

        if os.path.isdir(speaker_path):
            for audio_file in os.listdir(speaker_path):
                if audio_file.endswith('.wav'):
                    file_path = os.path.join(speaker_path, audio_file)
                    mfcc_features = extract_mfcc_pydub(file_path)
                    X.append(mfcc_features)
                    y.append(speaker_dir)  # Speaker name as the label

    # Convert lists to numpy arrays
    X = np.array(X)
    y = np.array(y)

    # Encode the speaker labels (names)
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)

    # Train an SVM model
    classifier = svm.SVC(kernel='linear')
    classifier.fit(X, y_encoded)

    return classifier, label_encoder


# 4. Use the trained model to identify speaker and transcribe speech
def identify_speaker_and_transcribe(audio_path, classifier, label_encoder):
    # Extract MFCC features for speaker identification
    mfcc_features = extract_mfcc_pydub(audio_path)

    # Identify speaker
    speaker_label = classifier.predict([mfcc_features])[0]
    speaker_name = label_encoder.inverse_transform([speaker_label])[0]

    # Perform speech-to-text transcription
    recognizer = sr.Recognizer()
    with sr.AudioFile(audio_path) as source:
        audio_data = recognizer.record(source)  # Read the audio data from the file

        try:
            # Recognize speech using Google's API
            text = recognizer.recognize_google(audio_data)
        except sr.UnknownValueError:
            text = "Could not understand audio"
        except sr.RequestError:
            text = "Could not request results from Google Speech Recognition service"

    return speaker_name, text


# 5. Record, Identify Speaker, and Transcribe in Real-Time
def record_identify_and_transcribe(duration=5, model=None, encoder=None):
    file_path = 'audioIn.wav'

    # Step 1: Record the audio in real-time


    # Step 2: Use the trained model to identify the speaker and transcribe speech
    speaker, transcription = identify_speaker_and_transcribe(file_path, model, encoder)

    # Output the results
    print(f"{speaker}")
    file = open(r"C:\Users\SREEJA\AppData\Local\Programs\furhat-sdk-desktop-launcher\Pro\output.txt","w")
    file.write(speaker)
    file.close()

    #print(f"Transcribed Text: {transcription}")


# Main Code: Train the model and then use it for real-time recording and processing
if __name__ == "__main__":
    # Set up the data directory containing speaker audio samples (for training)
    #file_path = r"C:\Users\SREEJA\Downloads\Speaker_Data\speaker1\audio_2.wav"
    #record_audio(file_path, duration=5)
    data_directory = r"C:\Users\SREEJA\Downloads\Speaker_Data"  # e.g., 'speaker_data/'

    # Train the speaker identification model
    model, encoder = train_speaker_model(data_directory)

    # Record in real-time, identify speaker, and transcribe speech

    record_identify_and_transcribe(duration=5, model=model, encoder=encoder)
    # extract_mfcc_pydub("audioIn.wav")
