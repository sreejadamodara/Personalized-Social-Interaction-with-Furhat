package furhatos.app.pro.flow

//import furhatos.app.pro.flow.main.Greeting
//import furhatos.app.pro.recordAudio
//import furhatos.app.pro.streamer
import furhatos.app.pro.flow.main.Idle
import furhatos.app.pro.setting.DISTANCE_TO_ENGAGE
import furhatos.app.pro.setting.MAX_NUMBER_OF_USERS
import furhatos.demo.audiofeed.FurhatAudioFeedPlayback
import furhatos.demo.audiofeed.FurhatAudioFeedRecorder
import furhatos.demo.audiofeed.FurhatAudioFeedStreamer
import furhatos.flow.kotlin.*
import furhatos.gestures.Gestures
import furhat.libraries.standard.GesturesLib
import java.io.File


//public val streamer = FurhatAudioFeedStreamer()
//
////    streamer.start("10.0.0.140")
//
////    println("Starting streaming, press return to stop.")
////    readlnOrNull()
////    streamer.stop()
////}
//
//fun recordAudio(streamer: FurhatAudioFeedStreamer) {
//    val recorder = FurhatAudioFeedRecorder(streamer)
//    recorder.startRecordSeparate(audioInFile = File("audioIn.wav"), audioOutFile = File("audioOut.wav"))
//}
//
//fun playbackAudio(streamer: FurhatAudioFeedStreamer) {
//    val playback = FurhatAudioFeedPlayback(streamer)
//    playback.start(playSystem = true, playUser = true)
//}
//
//val Init: State = state {
//    init {
//        /** Set our default interaction parameters */
//        users.setSimpleEngagementPolicy(DISTANCE_TO_ENGAGE, MAX_NUMBER_OF_USERS)
//    }
//    onEntry {
//        when (users.count) {
//            0 -> goto(Idle)
//            1 -> {
//                furhat.attend(users.random)
//                streamer.start("10.0.0.140")
//
//                // Start recording audio
//                recordAudio(streamer)
//                println("Recording Started")
//
//                // Listen for user input with specific trigger words
//                furhat.listen()
//            }
//            else -> goto(Idle)
//        }
//    }
//
//    onResponse {
//
//        val userSpeech = it.text.toLowerCase()
//
////        if (userSpeech.contains("hey ") || userSpeech.contains("hello ") || userSpeech.contains("hi ")) {
//        if (userSpeech.isNotEmpty()){
//            furhat.say("Hello! Let me check who you are.")
//
//            // Stop streaming
//            streamer.stop()
//            println("Recording stopped")
//
//            // Run the Python script using command-line command
//            runPythonScript()
//
//            // Extract the output from the Python script
//            val result = extractOutputFromFile("output.txt")
//            println("Result from Python script: $result")
//
//            // Greet the user with the result from the Python script (name recognition, etc.)
//            furhat.say("Hello $result! how are you")
//
//            // Proceed to the Greeting state
//            goto(Greeting)
//        } else {
//            furhat.say("I didn't understand that. Please say 'Hey'.")
//            furhat.listen()
//        }
//    }
//
//    onNoResponse {
//        furhat.say("I didn't hear anything. Please say 'Hey Furhat'.")
//        furhat.listen()
//    }
//}
//
//// Function to run a Python script using a command-line command
//fun runPythonScript() {
//    try {
//        val outputFile = File("output.txt")
//        val processBuilder = ProcessBuilder(
//            "python",
//            "C:\\Users\\SREEJA\\AppData\\Local\\Programs\\furhat-sdk-desktop-launcher\\Pro\\voice_model.py"
//        )
//        // Redirect output to output.txt
//        processBuilder.redirectOutput(outputFile)
//        val process = processBuilder.start()
//        process.waitFor() // Wait for the Python script to finish
//
//        println("Python script executed successfully.")
//    } catch (e: Exception) {
//        println("Error running Python script: ${e.message}")
//    }
//}
//
//// Function to read the result from output.txt
//fun extractOutputFromFile(filePath: String): String {
//    return try {
//        File(filePath).readText()
//    } catch (e: Exception) {
//        println("Error reading output file: ${e.message}")
//        ""
//    }
//}



// Variables and constants
var previousUser: String? = null
var hasGreeted: Boolean = false
public val streamer = FurhatAudioFeedStreamer()
val positiveKeywords = listOf("yes", "yeah", "sure", "ok", "all right")
val negativeKeywords = listOf("no", "not really", "another", "different", "change")

// Function to start audio recording
fun startRecording(streamer: FurhatAudioFeedStreamer) {
    val recorder = FurhatAudioFeedRecorder(streamer)
    recorder.startRecordSeparate(audioInFile = File("audioIn.wav"), audioOutFile = File("audioOut.wav"))
}

// Function to suggest a random topic
fun suggestRandomTopic(excludedTopics: List<String>): String {
    val topics = listOf("Health and Wellness", "Science and Technology", "Sports and Fitness", "Movies and Entertainment", "Travel and Adventure")
    val availableTopics = topics.filterNot { it in excludedTopics }
    return if (availableTopics.isNotEmpty()) availableTopics.random() else "No topics available"
}

// Function to run the script for user identification
fun runScript() {
    try {
        val outputFile = File("output.txt")
        val processBuilder = ProcessBuilder(
            "python",
            "C:\\Users\\SREEJA\\AppData\\Local\\Programs\\furhat-sdk-desktop-launcher\\Pro\\voice_model.py"
        )
        processBuilder.redirectOutput(outputFile)
        val process = processBuilder.start()
        process.waitFor()
        println("Voice recognition successful.")
    } catch (e: Exception) {
        println("Error running voice recognition script: ${e.message}")
    }
}

// Function to extract user identity from output.txt
fun extractOutputFromFile(filePath: String): String {
    return try {
        File(filePath).readText()
    } catch (e: Exception) {
        println("Error reading output file: ${e.message}")
        ""
    }
}

// Function to provide Furhat's response based on the detected emotion
fun getEmotionResponse(emotion: String): String {
    return when (emotion) {
        "happy" -> "I'm glad to hear that you're feeling happy!"
        "sad" -> "I'm sorry you're feeling down. If you'd like to talk about it, I'm here."
        "angry" -> "It sounds like you're upset. I hope things get better soon."
        "surprised" -> "That sounds surprising! Would you like to share more about it?"
        "afraid" -> "It sounds like something's worrying you. I'm here if you want to talk about it."
        else -> ""
    }
}

// Emotion analysis function that processes the user's input
fun analyzeEmotion(userResponse: String): String {
    return when {
        listOf("happy", "joy", "good", "great", "wonderful", "amazing", "excited", "pleased", "fantastic", "awesome", "content", "delighted", "cheerful", "smiling").any { it in userResponse } -> "happy"
        listOf("sad", "unhappy", "depressed", "not good", "bad", "upset", "down", "unfortunate", "disappointed", "gloomy", "heartbroken", "regretful", "miserable", "low").any { it in userResponse } -> "sad"
        listOf("angry", "mad", "furious", "upset", "frustrated", "annoyed", "irritated", "hate", "dislike", "fed up", "pissed off", "outraged", "bitter", "resentful").any { it in userResponse } -> "angry"
        listOf("surprised", "shocked", "amazed", "astonished", "incredible", "wow", "unexpected", "whoa", "stunned", "flabbergasted").any { it in userResponse } -> "surprised"
        listOf("afraid", "scared", "frightened", "nervous", "anxious", "worried", "uneasy", "terrified", "alarmed", "fearful").any { it in userResponse } -> "afraid"
        else -> "neutral"
    }
}

// Main Init state
val Init: State = state {
    init {
        users.setSimpleEngagementPolicy(DISTANCE_TO_ENGAGE, MAX_NUMBER_OF_USERS)
    }

    onEntry {
        when (users.count) {
            0 -> goto(Idle)
            1 -> {
                val currentUser = users.random
                furhat.attend(currentUser)
                streamer.start("10.0.0.140")
                startRecording(streamer)
                println("Recording Started")
                furhat.listen()
            }
            2 -> {
                val user1 = users.list[0]
                val user2 = users.list[1]

                // Interact with the first user
                furhat.attend(user1)
                streamer.start("10.0.0.140")
                startRecording(streamer)
                println("Recording Started for User 1")
                furhat.listen()
                onResponse {
                    streamer.stop()
                    println("Recording Stopped for User 1")
                    val userSpeech = it.text.lowercase().trim()
                    runScript()
                    val currentUser = extractOutputFromFile("output.txt").trim()
                    if (currentUser.isNotEmpty()) {
                        if (currentUser != previousUser) {
                            previousUser = currentUser
                            hasGreeted = false
                        }

                        if (!hasGreeted) {
                            furhat.say("Hello, \$currentUser! How are you doing?")
                            furhat.gesture(Gestures.BigSmile)
                            hasGreeted = true
                            furhat.listen()
                        } else {
                            val emotion = analyzeEmotion(userSpeech)
                            furhat.say(getEmotionResponse(emotion))
                            goto(SelectTopic(mutableListOf()))
                        }
                    } else {
                        furhat.say("Sorry, I couldn't identify you. Please try again.")
                        furhat.listen()
                    }
                }

                // Interact with the second user after interacting with the first
                onNoResponse {
                    furhat.attend(user2)
                    streamer.start("10.0.0.140")
                    startRecording(streamer)
                    println("Recording Started for User 2")
                    furhat.listen()
                    onResponse {
                        streamer.stop()
                        println("Recording Stopped for User 2")
                        val userSpeech = it.text.lowercase().trim()
                        runScript()
                        val currentUser = extractOutputFromFile("output.txt").trim()
                        if (currentUser.isNotEmpty()) {
                            if (currentUser != previousUser) {
                                previousUser = currentUser
                                hasGreeted = false
                            }

                            if (!hasGreeted) {
                                furhat.say("Hello, \$currentUser! How are you doing?")
                                furhat.gesture(Gestures.BigSmile)
                                hasGreeted = true
                                furhat.listen()
                            } else {
                                val emotion = analyzeEmotion(userSpeech)
                                furhat.say(getEmotionResponse(emotion))
                                goto(SelectTopic(mutableListOf()))
                            }
                        } else {
                            furhat.say("Sorry, I couldn't identify you. Please try again.")
                            furhat.listen()
                        }
                    }
                }

            }

            else -> {
                for (user in users.list) {
                    furhat.attend(user)
                    streamer.start("10.0.0.140")
                    startRecording(streamer)
                    println("Recording Started")
                    furhat.listen()
                }
            }
        }
    }

    onResponse {
        val userSpeech = it.text.lowercase().trim()
        streamer.stop()
        println("Recording Stopped")
        println("User said: $userSpeech")

        // Run script to identify the user
        runScript()
        val currentUser = extractOutputFromFile("output.txt").trim()

        if (currentUser.isNotEmpty()) {
            if (currentUser != previousUser) {
                previousUser = currentUser
                hasGreeted = false
            }

            if (!hasGreeted) {
                furhat.say("Hello, $currentUser! How are you doing?")
                furhat.gesture(Gestures.BigSmile)

                hasGreeted = true
                furhat.listen()
            } else {
                val emotion = analyzeEmotion(userSpeech)
                furhat.say(getEmotionResponse(emotion))

                // Start topic selection with an empty excludedTopics list
                goto(SelectTopic(mutableListOf()))
            }
        } else {
            furhat.say("Sorry, I couldn't identify you. Please try again.")
            furhat.listen()
        }
    }
}



// State to select and suggest a random topic
fun SelectTopic(excludedTopics: MutableList<String>): State = state {
    onEntry {
        val topic = suggestRandomTopic(excludedTopics)
        furhat.say("How about we talk about $topic?")
        goto(DiscussTopic(topic, excludedTopics))
    }
}

// State to handle user's response about the suggested topic
fun DiscussTopic(topic: String, excludedTopics: MutableList<String>): State = state {
    onEntry {
        furhat.listen()
    }

    onResponse {
        val userReply = it.text.lowercase().trim()

        if (positiveKeywords.any { keyword -> keyword in userReply }) {
            furhat.say("Great! Let's dive into $topic.")
            goto(DiscussSpecificTopic(topic))
        } else if (negativeKeywords.any { keyword -> keyword in userReply }) {
            excludedTopics.add(topic)
            goto(SelectTopic(excludedTopics))
        } else {
            furhat.say("I couldn't quite catch that. Let's try again.")
            reentry()
        }
    }
}

// State to discuss the selected topic with specific keyword-based responses
fun DiscussSpecificTopic(topic: String): State = state {

    var hasAskedAboutFollowup = false // Track if a follow-up question has been asked
    var hasRespondedToNeutral = false // Track if neutral response was acknowledged

    onEntry {
        when (topic) {
            "Health and Wellness" -> {
                val openingLines = listOf(
                    "Let's talk about health and wellness. It’s such an essential topic! Have you been doing anything recently to take care of your health?",
                    "Health and wellness are so important! Have you been focusing on any particular health goals lately?",
                    "Taking care of ourselves is key. Have you tried anything new for your health recently?"
                )
                furhat.say(openingLines.random())
                furhat.listen()
            }
            "Science and Technology" -> {
                val openingLines = listOf(
                    "Let’s dive into the world of science and technology! It’s incredible how quickly things are evolving. Have you heard about any new tech developments recently?",
                    "Science and technology are advancing rapidly. Have you come across any interesting innovations recently?",
                    "The tech world moves so fast! Is there any new technology that has caught your attention lately?"
                )
                furhat.say(openingLines.random())
                furhat.listen()
            }
            "Travel and Adventure" -> {
                val openingLines = listOf(
                    "Let’s talk about travel and adventure! Have you been on any trips recently?",
                    "Traveling can be such a thrilling experience. Have you explored any new places lately?",
                    "Adventure awaits! Have you gone on any trips or are you planning one soon?"
                )
                furhat.say(openingLines.random())
                furhat.listen()
            }
            "Movies and Entertainment" -> {
                val openingLines = listOf(
                    "Let's dive into movies and entertainment! Have you seen any good movies or shows recently?",
                    "Movies and shows can be such a great escape. Do you have any favorites you've watched lately?",
                    "Entertainment keeps us connected to stories from around the world. What have you been watching recently?"
                )
                furhat.say(openingLines.random())
                furhat.listen()
            }
            else -> furhat.say("I'm here to chat about anything!")
        }
    }

    onResponse {
        val userReply = it.text.lowercase().trim()

        // Define keyword-based responses for each topic
        val keywordResponses = when (topic) {
            "Health and Wellness" -> mapOf(
                "exercise" to "Exercise is a great way to boost both physical and mental health! What type of exercise do you enjoy most?",
                "diet" to "A balanced diet can make such a difference in our energy levels and mood. Do you have any favorite healthy foods?",
                "sleep" to "Getting enough sleep is crucial for overall health. Do you manage to get a good night's sleep?",
                "stress" to "Managing stress can be challenging. How do you usually cope with stress?",
                "water" to "Staying hydrated is so important! Do you try to drink a certain amount of water each day?",
                "walking" to "Walking is a wonderful low-impact exercise! How often do you go for walks?"
            )
            "Science and Technology" -> mapOf(
                "ai" to "Artificial Intelligence is transforming so many fields. What excites you most about AI?",
                "robotics" to "Robotics is a fascinating area. Robots are becoming more advanced every day! What’s your opinion on them?",
                "space" to "Space exploration is making big strides! Some companies are even working on sending tourists to space. Would you be interested in going to space someday?",
                "vr" to "Virtual Reality is such an immersive technology. Have you tried any VR experiences?",
                "tech" to "Technology is everywhere now! Do you have a favorite tech gadget?",
                "science" to "Science is the foundation of so much innovation. Do you follow any particular field of science?"
            )
            "Travel and Adventure" -> mapOf(
                "beach" to "A beach vacation sounds relaxing! There’s nothing like unwinding by the ocean. Do you have a favorite beach destination?",
                "hiking" to "Hiking is such a rewarding way to explore nature. Do you have any favorite hiking spots?",
                "camping" to "Camping is a great way to connect with nature. Are you more of a mountain camper or a beach camper?",
                "city" to "Cities offer so much energy and excitement! Do you have a favorite city you've visited?",
                "food" to "Trying local food is one of the best parts of traveling! Did you try any special dishes on your last trip?",
                "culture" to "Experiencing different cultures is so enriching. Have you visited any culturally unique places recently?"
            )
            "Movies and Entertainment" -> mapOf(
                "movie" to "Movies can be such a great escape! Do you have a favorite genre?",
                "show" to "TV shows can be so addictive! Are you watching anything interesting right now?",
                "actor" to "There are so many talented actors out there! Do you have a favorite actor or actress?",
                "series" to "TV series can pull us into incredible stories. Do you have a favorite series?",
                "comedy" to "Comedies are perfect for a good laugh! What’s the funniest show or movie you’ve seen recently?",
                "drama" to "Dramas can be so immersive. Is there a dramatic series or movie you really enjoyed?"
            )
            else -> emptyMap()
        }

        // Check for keywords in the user's response
        val matchedResponse = keywordResponses.entries.find { (keyword, _) -> keyword in userReply }?.value
        if (matchedResponse != null) {
            furhat.say(matchedResponse)
            furhat.listen() // Continue listening after a keyword-based response
        } else if (listOf("yes", "yeah", "sure", "absolutely", "definitely", "i have", "i’m trying").any { keyword -> keyword in userReply }) {
            if (!hasAskedAboutFollowup) {
                val positiveLines = when (topic) {
                    "Health and Wellness" -> listOf(
                        "That’s fantastic! Staying active is so important. What type of exercise do you enjoy most?",
                        "That’s great to hear! Keeping active can have such a positive impact. What’s your favorite way to stay fit?",
                        "Wonderful! Exercise can be a great way to boost energy. What’s your go-to workout?"
                    )
                    "Science and Technology" -> listOf(
                        "That’s amazing! The tech world is fascinating. What technology are you most excited about?",
                        "Great! So much is happening in tech. What’s the most interesting technology you’ve heard about?",
                        "That's awesome! The advancements are incredible. Do you have a favorite tech innovation?"
                    )
                    "Travel and Adventure" -> listOf(
                        "That sounds fantastic! Where did you go on your last trip?",
                        "Amazing! Traveling brings so many new experiences. Did you have a favorite part of your recent trip?",
                        "That’s wonderful! Exploring new places is so enriching. Do you have a favorite destination?"
                    )
                    "Movies and Entertainment" -> listOf(
                        "That’s awesome! What kind of movies or shows do you enjoy the most?",
                        "Sounds great! Do you prefer watching at the cinema or at home?",
                        "That’s fantastic! Do you have a favorite film or series that you could watch over and over?"
                    )
                    else -> listOf()
                }
                furhat.say(positiveLines.random())
                hasAskedAboutFollowup = true
                furhat.listen()
            } else {
                furhat.say("It's great to hear about your interest in this topic!")
                goto(EndConversation(topic))
            }
        } else {
            furhat.say("Thanks for sharing! Every bit of insight adds to the experience.")
            goto(EndConversation(topic))
        }
    }

    onReentry {
        if (!hasRespondedToNeutral) {
            furhat.say("That's okay! Every little insight is valuable.")
            goto(EndConversation(topic))
        }
    }
}





// Define EndConversation as a separate function so it can be called with a specific topic
fun EndConversation(topic: String): State = state {
    onEntry {
        val closingRemarks = when (topic) {
            "Health and Wellness" -> listOf(
                "It was great chatting about health. Stay well and keep up the good work!",
                "Thanks for discussing wellness with me! Every small step counts.",
                "I enjoyed our health talk! Take care and stay healthy!"
            )
            "Science and Technology" -> listOf(
                "Science and technology are shaping the future. Thanks for discussing it with me!",
                "It was fascinating to talk about tech! Stay curious and keep exploring new advancements.",
                "Thanks for sharing your thoughts on technology. There's always something new to learn!"
            )
            "Travel and Adventure" -> listOf(
                "Thanks for chatting about travel! I hope you have many more adventures ahead.",
                "It was wonderful to discuss travel and adventure with you. May your journeys be exciting!",
                "Thanks for sharing your travel dreams. Adventure awaits around every corner!"
            )
            "Movies and Entertainment" -> listOf(
                "Thanks for chatting about movies and entertainment! Enjoy watching your favorite shows.",
                "It was fun discussing movies and shows with you. May you find many more exciting stories to enjoy!",
                "I enjoyed our entertainment talk! Keep discovering new movies and shows!"
            )
            else -> listOf("Thanks for the chat! It was great talking with you.")
        }
        furhat.say(closingRemarks.random())
        goto(Idle)
    }
}









