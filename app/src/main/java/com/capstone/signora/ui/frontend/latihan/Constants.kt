package com.capstone.signora.ui.frontend.latihan

import com.capstone.signora.R

object Constants {
    const val USER_NAME: String = "user_name"
    const val TOTAL_QUESTIONS: String = "total_questions"
    const val SCORE: String = "score"
    const val QUESTION_TYPE: String = "question_type"

    const val TYPE_MUDAH: String = "mudah"
    const val TYPE_SEDANG: String = "sedang"
    const val TYPE_SULIT: String = "sulit"

    fun getQuestions(type: String): ArrayList<Question> {
        val questionsList = ArrayList<Question>()

        when (type) {
            TYPE_MUDAH -> {//Ferdinand
                questionsList.add(Question(1, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.m_isyarat, arrayListOf("M", "A", "B", "W"), 0))
                questionsList.add(Question(2, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.c_isyarat, arrayListOf("S", "A", "C", "I"), 2))
                questionsList.add(Question(3, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.d_isyarat, arrayListOf("Z", "H", "E", "D"), 3))
                questionsList.add(Question(4, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.j_isyarat, arrayListOf("W", "J", "X", "B"), 1))
                questionsList.add(Question(5, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.z_isyarat, arrayListOf("K", "A", "Z", "P"), 2))
                questionsList.add(Question(6, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.y_isyarat, arrayListOf("Y", "S", "L", "O"), 0))
                questionsList.add(Question(7, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.u_isyarat, arrayListOf("V", "T", "U", "Q"), 2))
                questionsList.add(Question(8, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.p_isyarat, arrayListOf("D", "U", "I", "P"), 3))
                questionsList.add(Question(9, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.x_isyarat, arrayListOf("Z", "X", "S", "B"), 1))
                questionsList.add(Question(10, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.w_isyarat, arrayListOf("W", "T", "Y", "L"), 0))
            }
            TYPE_SEDANG -> {
                questionsList.add(Question(1, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.m_isyarat, arrayListOf("M", "A", "B", "W"), 0))
                questionsList.add(Question(2, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.c_isyarat, arrayListOf("S", "A", "C", "I"), 2))
                questionsList.add(Question(3, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.d_isyarat, arrayListOf("Z", "H", "E", "D"), 3))
                questionsList.add(Question(4, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.j_isyarat, arrayListOf("W", "J", "X", "B"), 1))

            }
            TYPE_SULIT -> {
                questionsList.add(Question(1, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", 0, arrayListOf("M", "A", "B", "W"), 0, isVideo = true, mediaUri = "android.resource://com.capstone.signora/" + R.raw.output))
                questionsList.add(Question(2, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", 1, arrayListOf("M", "A", "B", "W"), 0, isVideo = true, mediaUri = "android.resource://com.capstone.signora/" + R.raw.fer))
                questionsList.add(Question(3, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.d_isyarat, arrayListOf("Z", "H", "E", "D"), 3))
                questionsList.add(Question(4, "Pilihlah salah satu alphabet yang mendefinisikan dari bahasa isyarat tersebut?", R.drawable.j_isyarat, arrayListOf("W", "J", "X", "B"), 1))

            }
        }

        return questionsList
    }
}