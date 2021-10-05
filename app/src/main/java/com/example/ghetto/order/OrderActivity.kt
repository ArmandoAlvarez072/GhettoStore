package com.example.ghetto.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ghetto.Constants
import com.example.ghetto.R
import com.example.ghetto.chat.ChatFragment
import com.example.ghetto.databinding.ActivityOrderBinding
import com.example.ghetto.entities.Order
import com.example.ghetto.track.TrackFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class OrderActivity : AppCompatActivity() , OnOrderListener , OrderAux {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderAdapter
    private lateinit var orderSelected : Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        setUpFirestore()
    }

    private fun setUpRecyclerView() {
        adapter = OrderAdapter(mutableListOf(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun setUpFirestore(){
        FirebaseAuth.getInstance().currentUser?.let{ user ->
            val db = FirebaseFirestore.getInstance()
            db.collection(Constants.COLL_REQUEST)
                .orderBy(Constants.PROP_DATE, Query.Direction.DESCENDING)
                .whereEqualTo(Constants.PROP_CLIENT_ID, user.uid )
                //.whereIn(Constants.PROP_STATUS, listOf(1, 2))
                //.whereNotIn(Constants.PROP_STATUS, listOf(1, 2))
                //.whereGreaterThan(Constants.PROP_STATUS, 1)
                //.whereLessThan(Constants.PROP_STATUS, 4)
                //.whereGreaterThanOrEqualTo(Constants.PROP_STATUS, 1)
                //.whereLessThanOrEqualTo(Constants.PROP_STATUS, 4)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val order = document.toObject(Order::class.java)
                        order.id = document.id
                        adapter.add(order)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al consultar datos", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onTrack(order: Order) {
        orderSelected = order
        val fragment = TrackFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onStartChat(order: Order) {
        orderSelected = order

        val fragment = ChatFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()

    }

    override fun getOrderSelecter(): Order = orderSelected
}