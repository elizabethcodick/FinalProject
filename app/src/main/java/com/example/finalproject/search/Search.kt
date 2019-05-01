package com.example.finalproject.search


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalproject.MainActivity
import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_search.*

class Search : Fragment() {
    private var list: ArrayList<String>? = null
    public var results: ArrayList<Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).searchArray()
        //searchButton.setOnClickListener { (activity as MainActivity).searchForRecipe() }
    }

}
