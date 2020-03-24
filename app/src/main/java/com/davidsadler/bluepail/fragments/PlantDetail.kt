package com.davidsadler.bluepail.fragments

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.ColorsAdapter
import com.davidsadler.bluepail.adapters.OnColorSelectedListener
import com.davidsadler.bluepail.model.Plant
import com.davidsadler.bluepail.model.PlantRepository
import com.davidsadler.bluepail.model.PlantViewModel
import com.davidsadler.bluepail.util.*
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PlantDetail : Fragment(), OnColorSelectedListener, OnReminderUpdatedListener, TimePicker.OnTimeChangedListener, View.OnClickListener {

    override fun onClick(v: View?) {
        dispatchTakePictureIntent()
    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        view?.let { timePicker ->
            val timePickerTag = timePicker.tag
            if (timePickerTag is Int) {
                when (timePickerTag) {
                    0 -> {
                        val dateAtCorrectTime = wateringDate!!.getDateAtDesiredTime(hourOfDay,minute)
                        wateringDate = dateAtCorrectTime
                        textView_next_watering_reminder.text = dateAtCorrectTime.toString()
                    }
                    1 -> {
                        val dateAtCorrectTime = fertilizingDate!!.getDateAtDesiredTime(hourOfDay,minute)
                        fertilizingDate = dateAtCorrectTime
                        textView_next_fertilizing.text = dateAtCorrectTime.toString()
                    }
                }
            }
        }
    }

    override fun onReminderUpdated(next: Date, interval: Int, isForFertilizing: Boolean) {
        setupReminderUi(next,interval,isForFertilizing)
    }

    override fun colorSelected(colorId: Int) {
        selectedColor = colorId
    }

    private var name = ""
    // TODO: needs to be red -- FIX MAGIC NUMBER PROBLEM HERE FOR COLOR
    private var selectedColor = -1754827
    private var wateringDate: Date? = null
    private var wateringInterval: Int? = null
    private var fertilizingDate: Date? = null
    private var fertilizingInterval: Int? = null
    private var plantId = 0
    private var photoUri: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    lateinit var alarmManager: AlarmManager
    private lateinit var viewModel: PlantViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.activity?.let {
            viewModel = ViewModelProvider(it).get(PlantViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_plant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAlarmManager()
        inflateBottomToolbar()
        setupToolbarItems()
        setupColorRecyclerView()
        setupReminderClickListeners()
        setupTimePickers()
        setupPhotoImageButton()
    }

    private fun setupAlarmManager() {
        this.activity?.let {
            alarmManager = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
    }

    private fun inflateBottomToolbar() {
        toolbar_cancel_save.inflateMenu(R.menu.detail_toolbar)
    }

    private fun setupToolbarItems() {
        toolbar_cancel_save.menu.findItem(R.id.menu_item_cancel).setOnMenuItemClickListener {
            navigateToPlantList()
            true
        }
        toolbar_cancel_save.menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {
            savePlant()
            true
        }
    }

    private fun setupColorRecyclerView() {
        this.context?.let { context ->
            recyclerView_plant_colors.layoutManager = GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
            recyclerView_plant_colors.adapter = ColorsAdapter(context,this)
        }
    }

    private fun setupReminderClickListeners() {
        // TODO: DRY
        this.view?.let {
            imageButton_setup_watering.setOnClickListener {
                val setupDialog = PlantReminderSetupDialog(this,false)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
            imageButton_setup_fertilizing.setOnClickListener{
                val setupDialog = PlantReminderSetupDialog(this,true)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
        }
    }

    private fun setupTimePickers() {
        timePicker_watering_time.setOnTimeChangedListener(this)
        timePicker_watering_time.tag = 0
        timePicker_setup_fertilizing.setOnTimeChangedListener(this)
        timePicker_setup_fertilizing.tag = 1
    }

    private fun setupPhotoImageButton() {
        imageButton_plant_photo.setOnClickListener(this)
    }

    private fun setupReminderUi(nextDate: Date, interval: Int, isForFertilizing: Boolean) {
        val intervalText = when (interval) {
            1 -> "$interval Day"
            else -> "$interval Days"
        }
        if (isForFertilizing) {
            fertilizingDate = nextDate
            fertilizingInterval = interval
            timePicker_setup_fertilizing.isVisible = true
            textView_next_fertilizing.text = nextDate.toString()
            textView_fertilizing_interval.text = intervalText
        } else {
            wateringDate = nextDate
            wateringInterval = interval
            timePicker_watering_time.isVisible = true
            textView_next_watering_reminder.text = nextDate.toString()
            textView_watering_interval.text = intervalText
        }
    }

    @Throws(IOException::class)
    private fun createImageFile() : File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = this.activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply {
            photoUri = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    print("Creating Image File")
                    createImageFile()
                } catch (ex: IOException) {
                    println("ERROR: Error occurred while creating file. ${ex.localizedMessage}")
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(this.context!!, "com.example.android.fileprovider",it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            getAndSetPhoto()
        } else {
            Toast.makeText(this.context!!,"Plant photo not saved",Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAndSetPhoto() {
        val bitMappedImage = BitmapFactory.decodeFile(photoUri)
        val resizedBitmap = bitMappedImage.resize(250,250)
        imageButton_plant_photo.setImageBitmap(resizedBitmap)
    }

    private fun navigateToPlantList() {
        this.view?.let {
            Navigation.findNavController(it).navigate(R.id.plantList)
        }
    }

    private fun savePlant() {
        println("Save plant pressed")
        if (allRequiredParametersAreSet()) {
            val plant = Plant(plantId,editText_plant_name.text.toString(),
                selectedColor,
                wateringDate!!,
                wateringInterval!!,
                fertilizingDate,
                fertilizingInterval,
                photoUri)
            if (plantId == 0) {
                // TODO: Create alarm notifications
                NotificationManager.scheduleNotification(plant.name,
                    true,
                    plant.wateringDate,
                    this.context!!,
                    alarmManager)
                if (fertilizingDate != null) {
                    fertilizingDate?.let {
                        NotificationManager.scheduleNotification(plant.name,
                            false,
                            it,
                            this.context!!,
                            alarmManager)
                    }
                }
                viewModel.insert(plant)
            } else {
                viewModel.update(plant)
                // TODO: Cancel alarm notifications if there are any... somehow
            }
            navigateToPlantList()
        }
    }

    private fun allRequiredParametersAreSet() : Boolean {
        this.context?.let {context ->
            if (editText_plant_name.text.toString().isEmpty()) {
                Toast.makeText(context,"Please enter a plant name",Toast.LENGTH_SHORT).show()
                return false
            }
            if (wateringDate == null && wateringInterval == null) {
                Toast.makeText(context,"Please enter a watering schedule",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
}
