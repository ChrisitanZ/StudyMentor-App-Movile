package com.example.studymentor.UI.Student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studymentor.R
import com.example.studymentor.StudentCalendarActivity
import com.example.studymentor.adapter.ReviewAdapterStudent
import com.example.studymentor.apiservice.RetrofitClient
import com.example.studymentor.model.Review
import com.example.studymentor.model.Student
import com.example.studymentor.model.Tutor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentsReviewsListActivity : AppCompatActivity() {
    private lateinit var reviewAdapterStudent: ReviewAdapterStudent
    private lateinit var rvListTutors: RecyclerView
    private lateinit var tvStudentInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_students_reviews_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btHome = findViewById<ImageButton>(R.id.btHome)
        val btTutorList = findViewById<ImageButton>(R.id.btTutors)
        val btCalendar = findViewById<ImageButton>(R.id.btCalendar)
        val btPerfil = findViewById<ImageButton>(R.id.btPerfilEstudiante)
        val btSeeReviewsS = findViewById<Button>(R.id.btSeeReviewsS)
        tvStudentInfo = findViewById(R.id.tvStudentInfo)

        rvListTutors = findViewById(R.id.rvListTutors)
        rvListTutors.layoutManager = LinearLayoutManager(this@StudentsReviewsListActivity)

        btHome.setOnClickListener {
            val intent = Intent(this@StudentsReviewsListActivity, HomeStudentActivity::class.java)
            startActivity(intent)
        }

        btTutorList.setOnClickListener {
            val intent = Intent(this@StudentsReviewsListActivity, TutorListActivity::class.java)
            startActivity(intent)
        }

        btPerfil.setOnClickListener {
            val intent = Intent(this@StudentsReviewsListActivity, StudentProfileActivity::class.java)
            startActivity(intent)
        }

        btCalendar.setOnClickListener {
            val intent = Intent(this@StudentsReviewsListActivity, StudentCalendarActivity::class.java)
            startActivity(intent)
        }

        btSeeReviewsS.setOnClickListener {
            Log.d("StudentsReviewsList", "btSeeReviewsS clicked")
            fetchReviewsStudent()
        }

        fetchStudentName()
    }

    private fun fetchStudentName() {
        val studentId = 25 //Reemplazar por el adecuado

        val service = RetrofitClient.studentService
        service.getStudentById(studentId).enqueue(object : Callback<Student> {
            override fun onResponse(call: Call<Student>, response: Response<Student>) {
                if (response.isSuccessful) {
                    val student = response.body()
                    if (student != null) {
                        tvStudentInfo.text = "${student.name} ${student.lastname}"
                    }
                } else {
                    Toast.makeText(this@StudentsReviewsListActivity, "Error al obtener el tutor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Student>, t: Throwable) {
                Toast.makeText(this@StudentsReviewsListActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchReviewsStudent() {
        val service = RetrofitClient.reviewService
        service.getReviews().enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                if (response.isSuccessful) {
                    val reviews = response.body() ?: emptyList()
                    Log.d("StudentsReviewsList", "Reviews fetched: ${reviews.size}")
                    reviewAdapterStudent = ReviewAdapterStudent(reviews)
                    rvListTutors.adapter = reviewAdapterStudent
                    Log.d("StudentsReviewsList", "Adapter set")
                    rvListTutors.layoutManager = LinearLayoutManager(this@StudentsReviewsListActivity)
                    Log.d("StudentsReviewsList", "LayoutManager set")
                } else {
                    Log.e("StudentsReviewsList", "Error fetching reviews: ${response.message()}")
                    Toast.makeText(this@StudentsReviewsListActivity, "Error al obtener la lista de reseñas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Review>>, t: Throwable) {
                Log.e("StudentsReviewsList", "Error fetching reviews: ${t.message}")
                Toast.makeText(this@StudentsReviewsListActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
