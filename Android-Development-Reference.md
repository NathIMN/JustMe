# Android Mobile Application Development Reference Sheet

## Kotlin Basics

### Variables
```kotlin
var mutableVariable = "Can be changed"
val immutableVariable = "Cannot be changed"
```

### Basic Data Types
```kotlin
val number: Int = 42
val decimal: Double = 3.14
val text: String = "Hello World"
val isTrue: Boolean = true
val character: Char = 'A'
```

### Arrays
```kotlin
val names = arrayOf("Jane", "Ted", "Alice", "John")
val numbers = intArrayOf(3, 4, 3, 2, 1, 5, 6, 7, 3)
```

### Functions
```kotlin
fun calculateCircumference(radius: Double): Double {
    return 2 * Math.PI * radius
}

fun greetUser(name: String) {
    println("Hello, $name!")
}
```

### Control Flow
```kotlin
// If Expression
val grade = if (marks >= 75) "A"
           else if (marks >= 65) "B"
           else if (marks >= 50) "C"
           else "F"

// When Expression
when (value) {
    1 -> println("One")
    2 -> println("Two")
    else -> println("Other")
}

// For Loop
for (i in 1..10) {
    println(i)
}

// While Loop
while (condition) {
    // code
}
```

### Collections
```kotlin
// Immutable Collections
val list = listOf("apple", "banana", "cherry")
val set = setOf(1, 2, 3)
val map = mapOf("key1" to "value1", "key2" to "value2")

// Mutable Collections
val mutableList = mutableListOf<String>()
val mutableSet = mutableSetOf<Int>()
val mutableMap = mutableMapOf<String, String>()
```

## Object-Oriented Programming

### Classes and Objects
```kotlin
class Bird {
    var type = ""
    var color = ""
}

// With Constructor
class Bird(var type: String, var color: String)

// Usage
val bird = Bird("Parrot", "Green")
println(bird.type)
```

### Inheritance
```kotlin
open class Animal {
    open fun makeSound() {
        println("Some sound")
    }
}

class Dog : Animal() {
    override fun makeSound() {
        println("Woof!")
    }
}
```

### Interfaces
```kotlin
interface Vehicle {
    fun start()
    fun stop()
}

class Car : Vehicle {
    override fun start() {
        println("Car started")
    }

    override fun stop() {
        println("Car stopped")
    }
}
```

### Data Classes
```kotlin
data class Book(
    val title: String,
    val author: String,
    val description: String
)
```

### Sealed Classes
```kotlin
sealed class ValidationResult {
    data class Empty(val errorMessage: String) : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
    object Valid : ValidationResult()
}
```

### Enum Classes
```kotlin
enum class Day(val dayOfWeek: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7)
}
```

## Android XML Layouts

### Basic Layout Structure
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Views go here -->

</LinearLayout>
```

### Common Views
```xml
<!-- TextView -->
<TextView
    android:id="@+id/textView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/hello_world"
    android:textSize="18sp"
    android:textColor="@color/black" />

<!-- EditText -->
<EditText
    android:id="@+id/editText"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:hint="Enter text here"
    android:inputType="text" />

<!-- Button -->
<Button
    android:id="@+id/button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Click Me"
    android:onClick="buttonClick" />

<!-- ImageView -->
<ImageView
    android:id="@+id/imageView"
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:src="@drawable/image"
    android:contentDescription="Description" />

<!-- CheckBox -->
<CheckBox
    android:id="@+id/checkBox"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="I agree to terms" />

<!-- Spinner -->
<Spinner
    android:id="@+id/spinner"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:entries="@array/options" />
```

### Layout Types

#### Linear Layout
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <!-- Children arranged vertically -->
</LinearLayout>
```

#### Constraint Layout
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Frame Layout
```xml
<FrameLayout
    android:id="@+id/fragmentContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <!-- Used for fragments -->
</FrameLayout>
```

#### RecyclerView
```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### Android Units
- **dp** (density-independent pixels) - for layout dimensions
- **sp** (scaled pixels) - for text sizes
- **px** (pixels) - actual screen pixels (avoid)
- **wrap_content** - size to fit content
- **match_parent** - fill available space

### Common Attributes
```xml
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:layout_margin="16dp"
android:padding="8dp"
android:background="@color/white"
android:gravity="center"
android:visibility="visible"
```

## Resources

### Strings (strings.xml)
```xml
<resources>
    <string name="app_name">My App</string>
    <string name="welcome_message">Welcome to the app!</string>

    <string-array name="colors">
        <item>Red</item>
        <item>Green</item>
        <item>Blue</item>
    </string-array>
</resources>
```

### Colors (colors.xml)
```xml
<resources>
    <color name="primary_color">#2196F3</color>
    <color name="accent_color">#FF5722</color>
    <color name="background_color">#FFFFFF</color>
</resources>
```

### Dimensions (dimens.xml)
```xml
<resources>
    <dimen name="margin_small">8dp</dimen>
    <dimen name="margin_medium">16dp</dimen>
    <dimen name="text_size_large">24sp</dimen>
</resources>
```

## Android Activities

### Basic Activity Structure
```kotlin
class MainActivity : AppCompatActivity() {

    // View declarations
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)

        // Set click listeners
        button.setOnClickListener {
            handleButtonClick()
        }
    }

    private fun handleButtonClick() {
        val text = editText.text.toString()
        textView.text = "Hello, $text!"
    }
}
```

### Toast Messages
```kotlin
Toast.makeText(this, "Message here", Toast.LENGTH_SHORT).show()
Toast.makeText(this, "Longer message", Toast.LENGTH_LONG).show()
```

### Alert Dialogs
```kotlin
fun showAlert(title: String, message: String) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("OK") { dialog, _ ->
        dialog.dismiss()
    }
    builder.setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
    }
    builder.create().show()
}
```

## Intents

### Explicit Intents
```kotlin
// Navigate to another activity
val intent = Intent(this, SecondActivity::class.java)
startActivity(intent)

// Pass data
val intent = Intent(this, SecondActivity::class.java)
intent.putExtra("USER_NAME", "John")
intent.putExtra("USER_AGE", 25)
startActivity(intent)

// Receive data in target activity
val name = intent.getStringExtra("USER_NAME")
val age = intent.getIntExtra("USER_AGE", 0)
```

### Implicit Intents
```kotlin
// Open web browser
val webpage = Uri.parse("https://www.example.com")
val intent = Intent(Intent.ACTION_VIEW, webpage)
startActivity(intent)

// Open dialer
val phoneUri = Uri.parse("tel:+1234567890")
val intent = Intent(Intent.ACTION_DIAL, phoneUri)
startActivity(intent)

// Send SMS
val smsUri = Uri.parse("smsto:+1234567890")
val intent = Intent(Intent.ACTION_SENDTO, smsUri)
intent.putExtra("sms_body", "Hello from my app!")
startActivity(intent)

// Send email
val intent = Intent(Intent.ACTION_SEND)
intent.type = "message/rfc822"
intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("test@example.com"))
intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
intent.putExtra(Intent.EXTRA_TEXT, "Email body")
startActivity(intent)

// Open camera
val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
startActivity(intent)
```

## Form Validation

### Validation Result Class
```kotlin
sealed class ValidationResult {
    data class Empty(val errorMessage: String) : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
    object Valid : ValidationResult()
}
```

### Form Data Class
```kotlin
class FormData(
    private val email: String,
    private val password: String
) {
    fun validateEmail(): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Empty("Email is required")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    fun validatePassword(): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Empty("Password is required")
            password.length < 6 -> ValidationResult.Invalid("Password must be at least 6 characters")
            else -> ValidationResult.Valid
        }
    }
}
```

### Validation Implementation
```kotlin
fun validateForm() {
    val formData = FormData(
        editTextEmail.text.toString(),
        editTextPassword.text.toString()
    )

    when (val emailValidation = formData.validateEmail()) {
        is ValidationResult.Valid -> {
            editTextEmail.error = null
        }
        is ValidationResult.Invalid -> {
            editTextEmail.error = emailValidation.errorMessage
        }
        is ValidationResult.Empty -> {
            editTextEmail.error = emailValidation.errorMessage
        }
    }
}
```

## RecyclerView Implementation

### Data Class
```kotlin
data class Post(
    val userName: String,
    val description: String,
    val likes: Int
)
```

### Item Layout (list_item_layout.xml)
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tvUserName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/tvDescription"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <Button
        android:id="@+id/btnLike"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Like" />

</LinearLayout>
```

### ViewHolder
```kotlin
class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
    val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    val btnLike: Button = itemView.findViewById(R.id.btnLike)
}
```

### Adapter
```kotlin
class PostAdapter(
    private val posts: List<Post>,
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvUserName.text = post.userName
        holder.tvDescription.text = post.description
        holder.btnLike.text = "${post.likes} Likes"

        holder.itemView.setOnClickListener {
            onItemClick(post)
        }

        holder.btnLike.setOnClickListener {
            // Handle like button click
        }
    }

    override fun getItemCount() = posts.size
}
```

### Setting up RecyclerView
```kotlin
class MainActivity : AppCompatActivity() {

    private val posts = listOf(
        Post("John Doe", "Hello World!", 10),
        Post("Jane Smith", "Android is awesome!", 25)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = PostAdapter(posts) { post ->
            // Handle item click
            Toast.makeText(this, "Clicked: ${post.userName}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
```

### Layout Managers
```kotlin
// Linear Layout Manager
recyclerView.layoutManager = LinearLayoutManager(this)

// Horizontal Linear Layout
recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

// Grid Layout Manager
recyclerView.layoutManager = GridLayoutManager(this, 2)
```

## Fragments

### Basic Fragment
```kotlin
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val button: Button = view.findViewById(R.id.button)
        button.setOnClickListener {
            Toast.makeText(requireContext(), "Fragment button clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
```

### Fragment Transaction
```kotlin
private fun loadFragment(fragment: Fragment) {
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()

    if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
        fragmentTransaction.add(R.id.fragmentContainer, fragment)
    } else {
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
    }

    fragmentTransaction.commit()
}
```

### Fragment Communication with ViewModel
```kotlin
class SharedViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
}

// In Fragment
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        viewModel.message.observe(viewLifecycleOwner) { message ->
            // Update UI with message
        }

        return view
    }
}
```

## ViewModel

### Basic ViewModel
```kotlin
class CounterViewModel : ViewModel() {
    private val _count = MutableLiveData<Int>().apply { value = 0 }
    val count: LiveData<Int> = _count

    fun increment() {
        _count.value = (_count.value ?: 0) + 1
    }

    fun decrement() {
        _count.value = (_count.value ?: 0) - 1
    }
}
```

### Using ViewModel in Activity
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView: TextView = findViewById(R.id.textView)
        val buttonIncrement: Button = findViewById(R.id.buttonIncrement)

        val viewModel = ViewModelProvider(this)[CounterViewModel::class.java]

        // Observe data changes
        viewModel.count.observe(this) { count ->
            textView.text = count.toString()
        }

        // Handle button clicks
        buttonIncrement.setOnClickListener {
            viewModel.increment()
        }
    }
}
```

## Singleton Pattern

### Data Storage Singleton
```kotlin
object UserCredentials {
    var username = ""
    var email = ""
    private var password = ""

    fun setPassword(password: String, confirmPassword: String): Boolean {
        return if (password == confirmPassword) {
            this.password = password
            true
        } else {
            false
        }
    }

    fun validatePassword(inputPassword: String): Boolean {
        return this.password == inputPassword
    }
}
```

### Book Manager Singleton
```kotlin
object BookManager {
    private val books = mutableListOf<Book>()

    fun addBook(book: Book) {
        books.add(book)
    }

    fun getBooks(): List<Book> {
        return books.toList()
    }

    fun updateBook(index: Int, book: Book) {
        if (index in books.indices) {
            books[index] = book
        }
    }

    fun deleteBook(index: Int) {
        if (index in books.indices) {
            books.removeAt(index)
        }
    }
}
```

## Common Patterns

### Activity Result Launcher
```kotlin
class MainActivity : AppCompatActivity() {

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }
}
```

### String Interpolation
```kotlin
val name = "John"
val age = 30
val message = "Hello, my name is $name and I am $age years old"
val calculation = "The result is ${10 + 5}"
```

### Null Safety
```kotlin
var nullable: String? = null

// Safe call operator
val length = nullable?.length

// Elvis operator
val lengthOrZero = nullable?.length ?: 0

// Let function
nullable?.let { value ->
    println("Value is not null: $value")
}
```

## Permissions (AndroidManifest.xml)

### Common Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.SEND_SMS" />

<uses-feature
    android:name="android.hardware.camera"
    android:required="true" />
```

## Best Practices

### Naming Conventions
- **Activities**: MainActivity, LoginActivity, etc.
- **Fragments**: HomeFragment, SettingsFragment, etc.
- **ViewModels**: MainViewModel, UserViewModel, etc.
- **Layouts**: activity_main.xml, fragment_home.xml, item_book.xml
- **IDs**: Use descriptive names like btnSubmit, edtUsername, tvTitle

### Resource Management
- Keep strings in strings.xml for localization
- Define colors in colors.xml for consistency
- Use dimensions in dimens.xml for responsive design
- Organize drawables by type and size

### Performance Tips
- Use ViewBinding instead of findViewById when possible
- Implement proper RecyclerView patterns for large datasets
- Use appropriate image loading libraries for network images
- Minimize memory leaks by properly managing lifecycle

### Code Organization
- Follow MVVM architecture pattern
- Separate business logic from UI logic
- Use data classes for model objects
- Implement proper error handling and validation
