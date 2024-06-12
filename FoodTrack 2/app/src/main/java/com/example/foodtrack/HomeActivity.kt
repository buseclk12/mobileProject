package com.example.foodtrack

import CustomWorker
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.foodtrack.database.RecipeDatabase
import com.example.foodtrack.database.RecipeModel
import com.example.foodtrack.util.JsonHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    var selectedIndex = 0
    var selectedCategory: String? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var exitButton: ImageView
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(emptyMap()) { recipe -> showRecipeDialog(recipe) }
        favoriteAdapter = FavoriteAdapter(emptyList()) { recipe -> updateRecipeFavorite(recipe) }
        recyclerView.adapter = categoryAdapter

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    fetchRecipesFromDatabase()
                    selectedIndex = 0
                }
                R.id.nav_search -> {
                    fetchFavoriteRecipes()
                    selectedIndex = 1
                }
                R.id.nav_camera -> {
                    if (selectedCategory != null) {
                        fetchSelectedCategoryRecipes()
                    }
                    selectedIndex = 2
                }
                R.id.nav_profile -> selectedIndex = 3
            }
            true
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                Toast.makeText(this@HomeActivity, "Double tap detected", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        val myWorkRequest = OneTimeWorkRequest.Builder(CustomWorker::class.java).build()
        WorkManager.getInstance(this).enqueue(myWorkRequest)

        fetchRecipesFromAssets()

        exitButton = findViewById(R.id.exit_button)
        exitButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        if (selectedIndex == 0) {
            fetchRecipesFromDatabase()
        } else if (selectedIndex == 1) {
            fetchFavoriteRecipes()
        } else if (selectedIndex == 2 && selectedCategory != null) {
            fetchSelectedCategoryRecipes()
        }
    }

    private fun fetchRecipesFromAssets() {
        val json = JsonHelper.loadJSONFromAsset(this, "recipes.json")
        if (json != null) {
            val recipes = JsonHelper.parseRecipes(json)
            saveRecipesToDatabase(recipes)
        } else {
            Toast.makeText(this, "Failed to load recipes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecipesToDatabase(recipes: List<RecipeModel>) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                recipeDao.deleteAll() // Eski verileri temizle
                recipes.forEach { recipe ->
                    if (recipe.name.isNotBlank() && recipe.category.isNotBlank()) {
                        recipeDao.insertData(recipe)
                    } else {
                        Log.e("HomeActivity", "Skipping recipe with missing fields: $recipe")
                    }
                }
            }
            fetchRecipesFromDatabase()
        }
    }

    private fun fetchRecipesFromDatabase() {
        lifecycleScope.launch {
            val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
            val recipes = withContext(Dispatchers.IO) {
                recipeDao.getAllData()
            }
            categoryAdapter.updateCategories(recipes.groupBy { it.category })
            recyclerView.adapter = categoryAdapter
        }
    }

    private fun fetchFavoriteRecipes() {
        lifecycleScope.launch {
            val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
            val favoriteRecipes = withContext(Dispatchers.IO) {
                recipeDao.getFavoriteRecipes()
            }
            favoriteAdapter.updateFavorites(favoriteRecipes)
            recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
            recyclerView.adapter = favoriteAdapter
        }
    }

    fun fetchSelectedCategoryRecipes() {
        selectedCategory?.let { category ->
            lifecycleScope.launch {
                val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                val recipes = withContext(Dispatchers.IO) {
                    recipeDao.getAllData().filter { it.category == category }
                }
                favoriteAdapter.updateFavorites(recipes)
                recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
                recyclerView.adapter = favoriteAdapter
            }
        }
    }

    fun updateRecipeFavorite(recipe: RecipeModel) {
        mediaPlayer = MediaPlayer.create(this, R.raw.update_sound)
        mediaPlayer.start()
        recipe.favorite = !recipe.favorite
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                recipeDao.updateData(recipe)
            }
            withContext(Dispatchers.Main) {
                val message = if (recipe.favorite) {
                    "Recipe added to favorites"
                } else {
                    "Recipe removed from favorites"
                }
                Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
                fetchRecipesFromDatabase()
            }
        }
    }

    private fun deleteRecipe(recipe: RecipeModel) {
        mediaPlayer = MediaPlayer.create(this, R.raw.delete_sound)
        mediaPlayer.start()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                recipeDao.deleteData(recipe)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, "Recipe deleted successfully", Toast.LENGTH_SHORT).show()
                fetchRecipesFromDatabase()
            }
        }
    }

    private fun showRecipeDialog(recipe: RecipeModel) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_recipe_detail, null)
        builder.setView(view)

        val nameTextView = view.findViewById<TextView>(R.id.dialog_recipe_name)
        val ingredientsTextView = view.findViewById<TextView>(R.id.dialog_recipe_ingredients)
        val stepsTextView = view.findViewById<TextView>(R.id.dialog_recipe_steps)
        val closeButton = view.findViewById<Button>(R.id.dialog_close_button)
        val updateButton = view.findViewById<Button>(R.id.dialog_update_button)
        val deleteButton = view.findViewById<Button>(R.id.dialog_delete_button)  // Add delete button
        val imageView = view.findViewById<ImageView>(R.id.dialog_recipe_image)

        nameTextView.text = recipe.name
        ingredientsTextView.text = recipe.ingredients.joinToString("\n")
        stepsTextView.text = recipe.instructions.joinToString("\n")
        Picasso.get().load(recipe.image).into(imageView)

        val dialog = builder.create()

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        updateButton.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, UpdateRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            dialog.dismiss()
            deleteRecipe(recipe)
        }

        dialog.show()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (gestureDetector.onTouchEvent(it)) {
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

class CategoryAdapter(private var categories: Map<String, List<RecipeModel>>, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories.keys.toList()[position]
        holder.bind(category, categories[category] ?: emptyList())
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun updateCategories(newCategories: Map<String, List<RecipeModel>>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}

class CategoryViewHolder(itemView: View, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView: TextView = itemView.findViewById(R.id.category_title)
    private val seeAllButton: Button = itemView.findViewById(R.id.see_all_button)
    private val recyclerView: RecyclerView = itemView.findViewById(R.id.food_recycler_view)

    fun bind(category: String, recipes: List<RecipeModel>) {
        titleTextView.text = category
        seeAllButton.setOnClickListener {
            (itemView.context as HomeActivity).selectedCategory = category
            (itemView.context as HomeActivity).selectedIndex = 2
            (itemView.context as HomeActivity).fetchSelectedCategoryRecipes()
            (itemView.context as HomeActivity).bottomNavigationView.selectedItemId = R.id.nav_camera
        }

        recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = FoodAdapter(recipes, itemClickListener)
    }
}

class FoodAdapter(private val recipes: List<RecipeModel>, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.Adapter<FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return FoodViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}

class FoodViewHolder(itemView: View, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val foodTitleTextView: TextView = itemView.findViewById(R.id.food_title)
    private val foodImageView: ImageView = itemView.findViewById(R.id.food_image)
    private val commentsTextView: TextView = itemView.findViewById(R.id.comments_text)
    private val likesTextView: TextView = itemView.findViewById(R.id.likes_text)

    init {
        foodTitleTextView.setOnLongClickListener {
            val recipe = itemView.tag as RecipeModel
            (itemView.context as HomeActivity).updateRecipeFavorite(recipe)
            true
        }
    }

    fun bind(recipe: RecipeModel) {
        foodTitleTextView.text = recipe.name
        Picasso.get().load(recipe.image).into(foodImageView)
        commentsTextView.text = "Yorum"
        likesTextView.text = "Beğeni"

        foodImageView.setOnClickListener {
            itemClickListener(recipe)
        }
        itemView.tag = recipe
    }
}

class FavoriteAdapter(private var favorites: List<RecipeModel>, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.Adapter<FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view, itemClickListener)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    fun updateFavorites(newFavorites: List<RecipeModel>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }
}

class FavoriteViewHolder(itemView: View, private val itemClickListener: (RecipeModel) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val foodTitleTextView: TextView = itemView.findViewById(R.id.food_title)
    private val foodImageView: ImageView = itemView.findViewById(R.id.food_image)

    init {
        itemView.setOnLongClickListener {
            val recipe = itemView.tag as RecipeModel
            (itemView.context as HomeActivity).updateRecipeFavorite(recipe)
            true
        }
    }

    fun bind(recipe: RecipeModel) {
        foodTitleTextView.text = recipe.name
        Picasso.get().load(recipe.image).into(foodImageView)
        itemView.tag = recipe
    }
}
