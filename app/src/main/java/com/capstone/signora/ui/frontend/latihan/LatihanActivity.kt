package com.capstone.signora.ui.frontend.latihan

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.capstone.signora.R

class LatihanActivity : AppCompatActivity() {
    private var userName: String? = null
    private var questionType: String? = null

    private lateinit var questionsList: ArrayList<Question>
    private var currentQuestionIndex = 0
    private var selectedAlternativeIndex = -1
    private var isAnswerChecked = false
    private var totalScore = 0
    private val alternativesIds = arrayOf(R.id.optionOne, R.id.optionTwo, R.id.optionThree, R.id.optionFour)

    private var tvQuestion: TextView? = null
    private var ivImage: ImageView? = null
    private var vvVideo: VideoView? = null
    private var progressBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var btnSubmit: Button? = null
    private var tvAlternatives: ArrayList<TextView>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latihan)

        userName = intent.getStringExtra(Constants.USER_NAME)
        questionType = intent.getStringExtra(Constants.QUESTION_TYPE)

        questionsList = Constants.getQuestions(questionType ?: Constants.TYPE_MUDAH)

        tvQuestion = findViewById(R.id.tvQuestion)
        ivImage = findViewById(R.id.ivImage)
        vvVideo = findViewById(R.id.vvVideo)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvAlternatives = arrayListOf(
            findViewById(R.id.optionOne),
            findViewById(R.id.optionTwo),
            findViewById(R.id.optionThree),
            findViewById(R.id.optionFour),
        )

        updateQuestion()

        btnSubmit?.setOnClickListener {
            if (!isAnswerChecked) {
                val anyAnswerIsChecked = selectedAlternativeIndex != -1
                if (!anyAnswerIsChecked) {
                    Toast.makeText(this, "Tolong, pilih salah satu opsi", Toast.LENGTH_SHORT).show()
                } else {
                    val currentQuestion = questionsList[currentQuestionIndex]
                    if (selectedAlternativeIndex == currentQuestion.correctAnswerIndex) {
                        answerView(tvAlternatives!![selectedAlternativeIndex], R.drawable.correct_option_border_bg)
                        totalScore++
                    } else {
                        answerView(tvAlternatives!![selectedAlternativeIndex], R.drawable.wrong_option_border_bg)
                        answerView(tvAlternatives!![currentQuestion.correctAnswerIndex], R.drawable.correct_option_border_bg)
                    }

                    isAnswerChecked = true
                    btnSubmit?.text = if (currentQuestionIndex == questionsList.size - 1) "Selesai" else "MENUJU PERTANYAAN SELANJUTNYA"
                    selectedAlternativeIndex = -1
                }
            } else {
                if (currentQuestionIndex < questionsList.size - 1) {
                    currentQuestionIndex++
                    updateQuestion()
                } else {
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(Constants.USER_NAME, userName)
                    intent.putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)
                    intent.putExtra(Constants.SCORE, totalScore)
                    startActivity(intent)
                    finish()
                }

                isAnswerChecked = false
            }
        }

        tvAlternatives?.let {
            for (optionIndex in it.indices) {
                it[optionIndex].let {
                    it.setOnClickListener {
                        if (!isAnswerChecked) {
                            selectedAlternativeView(it as TextView, optionIndex)
                        }
                    }
                }
            }
        }
    }

    private fun updateQuestion() {
        defaultAlternativesView()

        // Render Question Text
        tvQuestion?.text = questionsList[currentQuestionIndex].questionText

        // Check if the question has an image or video
        val currentQuestion = questionsList[currentQuestionIndex]
        if (currentQuestion.isVideo) {
            ivImage?.visibility = ImageView.GONE
            vvVideo?.visibility = VideoView.VISIBLE
            vvVideo?.setVideoURI(Uri.parse(currentQuestion.mediaUri))
            vvVideo?.start()
            vvVideo?.setOnCompletionListener {
                vvVideo?.start()
            }
        } else {
            ivImage?.visibility = ImageView.VISIBLE
            vvVideo?.visibility = VideoView.GONE
            ivImage?.setImageResource(currentQuestion.image)
        }

        // progressBar
        progressBar?.progress = currentQuestionIndex + 1
        // Text of progress bar
        tvProgress?.text = "${currentQuestionIndex + 1}/${questionsList.size}"

        for (alternativeIndex in questionsList[currentQuestionIndex].alternatives.indices) {
            tvAlternatives!![alternativeIndex].text = questionsList[currentQuestionIndex].alternatives[alternativeIndex]
        }

        btnSubmit?.text = if (currentQuestionIndex == questionsList.size - 1) "SELESAI" else "JAWAB"
    }

    private fun defaultAlternativesView() {
        for (alternativeTv in tvAlternatives!!) {
            alternativeTv.typeface = Typeface.DEFAULT
            alternativeTv.setTextColor(Color.parseColor("#7A8089"))
            alternativeTv.background = ContextCompat.getDrawable(
                this@LatihanActivity,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedAlternativeView(option: TextView, index: Int) {
        defaultAlternativesView()
        selectedAlternativeIndex = index

        option.setTextColor(
            Color.parseColor("#363A43")
        )
        option.setTypeface(option.typeface, Typeface.BOLD)
        option.background = ContextCompat.getDrawable(
            this@LatihanActivity,
            R.drawable.selected_option_border_bg
        )
    }

    private fun answerView(view: TextView, drawableId: Int) {
        view.background = ContextCompat.getDrawable(
            this@LatihanActivity,
            drawableId
        )
        tvAlternatives!![selectedAlternativeIndex].setTextColor(
            Color.parseColor("#FFFFFF")
        )
    }
}