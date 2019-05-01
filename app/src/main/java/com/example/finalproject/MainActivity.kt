package com.example.finalproject

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.example.finalproject.favorites.FavoritesDetailsPage
import com.example.finalproject.favorites.FavoritesList
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import com.example.finalproject.search.Search
import com.example.finalproject.views.*
import kotlinx.android.synthetic.main.fragment_search.*
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.*


class MainActivity : AppCompatActivity() {

    public var currentNav: Int? = 0
    public var timerVis: Boolean? = false
    var mediaPlayer: MediaPlayer? = null

    var sp: SharedPreferences? = null
    public var currentTitle: String? = null
    public var recipeLoc: Int? = null
    public var savedRecipes = ArrayList<String>()
    public var searchResults = ArrayList<Int>()
    public var searchText = ArrayList<String>()
    public var contextOfApp: Context? = null

    public val myTitlesList = ArrayList<String>()
    public val myCookTimeList = ArrayList<Int>()
    public val myRatingsList = ArrayList<String>()
    public val myTimeList = ArrayList<String>()
    public val myImgList = ArrayList<String>()
    public val myLevelList = ArrayList<String>()

    public var myIngredientList = ArrayList<JSONArray>()
    public var myInstructionsList = ArrayList<JSONArray>()


    public var currentPosition: Int? = 0

    private var myListFragment: RecipeCards? = null
    private var favoritesList: FavoritesList? = null


    fun loadJSONFromAsset(){

        val json: String = applicationContext.assets.open("Recipes.json").bufferedReader().use{
            it.readText()
        }

        try {
            val titles = JSONArray(json)

            //Loop the Array
            for (i in 0 until titles.length()) {
                Log.e("Message", "loop")
                val map = HashMap<String, String>()
                val e = titles.getJSONObject(i)
                //map["id"] = "id"
                myTitlesList.add(e.getString("title"))
                myCookTimeList.add(e.getInt("cookTime"))
                myRatingsList.add(e.getString("rating"))
                myTimeList.add(e.getString("timeTotal"))
                myImgList.add(e.getString("imageLink"))
                myLevelList.add(e.getString("level"))
                //myIngredientList.add(e.getString("ingredients"))
                val foodJson = e.getJSONArray("ingredients")
                myIngredientList.add(foodJson!!)
                val instructJson = e.getJSONArray("instructions")
                myInstructionsList.add(instructJson!!)

            }
        } catch (e: JSONException) {
            Log.e("log_tag", "Error parsing data $e")
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.the_info, HomePageFragment())
                    .addToBackStack(null)
                    .commit()
                currentNav = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_recipes -> {
                loadFragment("List")
                currentNav = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_fav -> {
                loadFragment("Favorites")
                currentNav = 2
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search -> {
                loadFragment("Search")
                currentNav = 3
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    public fun loadFragment(which: String){
        if(which =="List") {
            myListFragment = RecipeCards()
            myListFragment?.setMyItemChangedListener(itemChangeListener)
            println("Assigning the listener")

            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            ) //clear backstack

            if(currentNav == 0){
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.the_info, myListFragment!!)
                    .addToBackStack(null)
                    .commit()
            } else{
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.the_info, myListFragment!!)
                    .addToBackStack(null)
                    .commit()
            }

        }
        if(which == "Details"){
            if(timerVis == true){
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.the_info, RecipeDetailsPage())
                    .addToBackStack(null)
                    .commit()
            } else{
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.the_info, RecipeDetailsPage())
                    .addToBackStack(null)
                    .commit()
            }

            timerVis = false

            supportFragmentManager.beginTransaction()
                .replace(R.id.detailsFrame, DetailsList()).commit()
            supportFragmentManager.beginTransaction()
                .replace(R.id.ingredientsFrame, IngredientsList()).commit()
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_image, DetailImage()).commit()
            supportFragmentManager.beginTransaction()
                .replace(R.id.instructionsFrame, InstructionsList()).commit()
        }
        if(which == "Timer") {
            timerVis = true
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.the_info, Timer())
                .addToBackStack(null)
                .commit()
        }
        if(which == "Favorites") {
            favoritesList = FavoritesList()
            favoritesList?.setMyItemChangedListener(favItemChangeListener)

            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            ) //clear backstack

            if(currentNav == 0 || currentNav == 1){
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.the_info, favoritesList!!)
                    .addToBackStack(null)
                    .commit()
            } else{
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.the_info, favoritesList!!)
                    .addToBackStack(null)
                    .commit()
            }

        }
        if(which =="Search") {
            myListFragment = RecipeCards()
            myListFragment?.setMyItemChangedListener(itemChangeListener)

            supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            ) //clear backstack

            if(currentNav == 3){
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.the_info, Search())
                    .addToBackStack(null)
                    .commit()
            }else{
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.the_info, Search())
                    .addToBackStack(null)
                    .commit()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.search_result, myListFragment!!)
                .commit()
        }
    }


    //listener for list fragment
    private var itemChangeListener: RecipeCards.ItemChangedListener =
        object : RecipeCards.ItemChangedListener {

            override fun onSelectedItemChanged(itemNameInt: Int) {
                currentPosition = itemNameInt

                loadFragment("Details")
            }

        }
    private var favItemChangeListener: FavoritesList.ItemChangedListener =
        object : FavoritesList.ItemChangedListener {

            override fun onSelectedItemChanged(itemNameInt: Int) {
                currentPosition = itemNameInt

                supportFragmentManager.beginTransaction().replace(R.id.the_info,
                    FavoritesDetailsPage()
                )
                    .commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.detailsFrame, DetailsList()).commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.ingredientsFrame, IngredientsList()).commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.detail_image, DetailImage()).commit()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.instructionsFrame, InstructionsList()).commit()
            }

        }

    private var adapter: ArrayAdapter<String>? = null


    fun searchArray(){
        val list = findViewById(R.id.theList) as ListView
        val theFilter = findViewById(R.id.searchInput) as EditText


        adapter = ArrayAdapter(this, R.layout.search_list,  myTitlesList )
        list.adapter = adapter

        list.setOnItemClickListener { parent, view, position, id ->

            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            currentPosition = myTitlesList.indexOf(list.getItemAtPosition(position))
            loadFragment("Details")

        }

        theFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                this@MainActivity.adapter?.getFilter()?.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }


        })
    }

    fun getIngredientsLength(): Int{
        return setIngredientsList().size
    }


    fun setIngredientsList(): ArrayList<String> {
        var currentIngredients = myIngredientList[currentPosition!!]

        val curr = currentIngredients

        val list = ArrayList<String>()
        for (i in 0 until curr!!.length()) {
            list.add(curr[i].toString())
        }
        return list
    }

    fun getInstructionLength(): Int{
        return setInstructionsList().size
    }

    fun setInstructionsList(): ArrayList<String> {
        var currentInstructions = myInstructionsList[currentPosition!!]

        val curr = currentInstructions

        val list = ArrayList<String>()
        for (i in 0 until curr!!.length()) {
            list.add(curr[i].toString())
        }
        return list
    }

    //Code taken from https://stackoverflow.com/questions/8924599/how-to-count-the-exact-number-of-words-in-a-string-that-has-empty-spaces-between
    fun wordCount(word: String?): Int {

        if (word == null || word.trim { it <= ' ' }.length == 0) {
            return 0
        }

        var counter = 1

        for (c in word.trim { it <= ' ' }.toCharArray()) {
            if (c == ' ') {
                counter++
            }
        }
        return counter
    }

    fun playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.analog_watch_alarm)
        mediaPlayer?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contextOfApp = getApplicationContext()
        sp = getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        //spEdit = sp.edit()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.the_info, HomePageFragment()).commit()
            loadJSONFromAsset()
        }
        val editor = sp?.edit()?.clear()?.commit()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
