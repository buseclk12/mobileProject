package com.example.foodtrack

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodtrack.database.RecipeDatabase
import com.example.foodtrack.database.RecipeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateRecipeActivity : AppCompatActivity() {

    private lateinit var editTextIngredients: EditText
    private lateinit var editTextSteps: EditText
    private lateinit var buttonSave: Button
    private lateinit var recipe: RecipeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_update_recipe)

        editTextIngredients = findViewById(R.id.editTextIngredients)
        editTextSteps = findViewById(R.id.editTextSteps)
        buttonSave = findViewById(R.id.buttonSave)

        recipe = intent.getSerializableExtra("recipe") as RecipeModel

        // Set current values
        editTextIngredients.setText(recipe.ingredients.joinToString("\n"))
        editTextSteps.setText(recipe.instructions.joinToString("\n"))

        buttonSave.setOnClickListener {
            updateRecipeInDatabase()
        }
    }

    private fun updateRecipeInDatabase() {
        val updatedIngredients = editTextIngredients.text.toString().split("\n")
        val updatedSteps = editTextSteps.text.toString().split("\n")

        recipe.ingredients = updatedIngredients
        recipe.instructions = updatedSteps

        //uygulama kapanınca hiçbir şey değişmemis gibi olması için.
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val recipeDao = RecipeDatabase.getDatabase(applicationContext).recipeDao()
                recipeDao.updateData(recipe)

                val updatedRecipe = recipeDao.getRecipeById(recipe.id)
                updatedRecipe?.let {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@UpdateRecipeActivity, "Updated Recipe: $updatedRecipe", Toast.LENGTH_LONG).show()
                    }
                }
            }
            //(UI güncellemeleri burada yapılır).
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UpdateRecipeActivity, "Recipe updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
