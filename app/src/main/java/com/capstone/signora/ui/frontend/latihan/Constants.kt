package com.capstone.signora.ui.frontend.latihan

import com.capstone.signora.R

object Constants {
    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val SCORE: String = "score"

    fun getQuestions(): ArrayList<Question> {
        val questionsList = ArrayList<Question>()

        // 1
        val questionOne = Question(
            1,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.m_isyarat,
            arrayListOf("M", "A", "B", "W"),
            0,
        )
        questionsList.add(questionOne)

        // 2
        val questionTwo = Question(
            2,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.c_isyarat,
            arrayListOf("S", "A", "C", "I"),
            2
        )
        questionsList.add(questionTwo)

        // 3
        val questionThree = Question(
            3,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.d_isyarat,
            arrayListOf("Z", "H", "E", "D"),
            3
        )
        questionsList.add(questionThree)

        // 4
        val questionFour = Question(
            4,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.j_isyarat,
            arrayListOf("W", "J", "X", "B"),
            1
        )
        questionsList.add(questionFour)

        // 5
        val questionFive = Question(
            5,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.z_isyarat,
            arrayListOf("K", "A", "Z", "P"),
            2
        )
        questionsList.add(questionFive)

        // 6
        val questionSix = Question(
            6,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.y_isyarat,
            arrayListOf("Y", "S", "L", "O"),
            0
        )
        questionsList.add(questionSix)

        // 7
        val questionSeven = Question(
            7,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.u_isyarat,
            arrayListOf("V", "T", "U", "Q"),
            2
        )
        questionsList.add(questionSeven)

        // 8
        val questionEight = Question(
            8,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.p_isyarat,
            arrayListOf("D", "U", "I", "P"),
            3
        )
        questionsList.add(questionEight)

        // 9
        val questionNine = Question(
            9,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.x_isyarat,
            arrayListOf("Z", "X", "S", "B"),
            1
        )
        questionsList.add(questionNine)

        // 10
        val questionTen = Question(
            10,
            "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?",
            R.drawable.w_isyarat,
            arrayListOf("W", "T", "Y", "L"),
            0
        )
        questionsList.add(questionTen)

        return questionsList
    }
}