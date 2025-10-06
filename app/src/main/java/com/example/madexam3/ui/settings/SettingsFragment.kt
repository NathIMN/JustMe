package com.example.madexam3.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.work.WorkManager
import com.example.madexam3.data.DataManager
import com.example.madexam3.databinding.FragmentSettingsBinding
import com.example.madexam3.notifications.HydrationNotificationWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Permission launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scheduleHydrationReminders()
        } else {
            // Reset the switch to off if permission denied
            binding.switchHydrationReminders.isChecked = false
            DataManager.setHydrationReminders(false)
            Toast.makeText(requireContext(), "Notification permission required for reminders", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        binding.apply {
            // Load theme preference
            val isDarkTheme = DataManager.getThemePreference()
            switchDarkTheme.isChecked = isDarkTheme

            // Load hydration settings
            val isHydrationEnabled = DataManager.isHydrationRemindersEnabled()
            switchHydrationReminders.isChecked = isHydrationEnabled

            val hydrationInterval = DataManager.getHydrationInterval()
            tvHydrationInterval.text = "$hydrationInterval hours"
            sliderHydrationInterval.value = hydrationInterval.toFloat()

            // Enable/disable hydration interval based on switch
            sliderHydrationInterval.isEnabled = isHydrationEnabled
            tvHydrationInterval.isEnabled = isHydrationEnabled
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            // Theme switch
            switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
                DataManager.setThemePreference(isChecked)
                applyTheme(isChecked)
            }

            // Hydration reminders switch
            switchHydrationReminders.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (checkNotificationPermission()) {
                        DataManager.setHydrationReminders(isChecked)
                        sliderHydrationInterval.isEnabled = isChecked
                        tvHydrationInterval.isEnabled = isChecked
                        scheduleHydrationReminders()
                    } else {
                        requestNotificationPermission()
                    }
                } else {
                    DataManager.setHydrationReminders(isChecked)
                    sliderHydrationInterval.isEnabled = isChecked
                    tvHydrationInterval.isEnabled = isChecked
                    cancelHydrationReminders()
                }
            }

            // Hydration interval slider
            sliderHydrationInterval.addOnChangeListener { _, value, _ ->
                val hours = value.toInt()
                DataManager.setHydrationInterval(hours)
                tvHydrationInterval.text = "$hours hours"

                if (DataManager.isHydrationRemindersEnabled()) {
                    scheduleHydrationReminders()
                }
            }

            // Generate test data button
            btnGenerateTestData.setOnClickListener {
                generateTestData()
            }

            // Reset data button
            btnResetData.setOnClickListener {
                showResetDataConfirmation()
            }
        }
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun scheduleHydrationReminders() {
        val interval = DataManager.getHydrationInterval().toLong()

        val workRequest = PeriodicWorkRequestBuilder<HydrationNotificationWorker>(
            interval, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "hydration_reminders",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        Toast.makeText(requireContext(), "Hydration reminders enabled", Toast.LENGTH_SHORT).show()
    }

    private fun cancelHydrationReminders() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("hydration_reminders")
        Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
    }

    private fun showResetDataConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Reset All Data")
            .setMessage(getString(com.example.madexam3.R.string.reset_confirmation))
            .setPositiveButton("Reset") { _, _ ->
                resetAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetAllData() {
        // Cancel any existing work
        WorkManager.getInstance(requireContext()).cancelAllWork()

        // Reset all data
        DataManager.resetAllData()

        // Reload settings to default state
        loadSettings()

        Toast.makeText(requireContext(), "All data has been reset", Toast.LENGTH_LONG).show()
    }

    private fun generateTestData() {
        try {
            DataManager.generateTestData()
            android.widget.Toast.makeText(
                requireContext(),
                "Test data generated successfully! You can now see habits and mood data from the past 30 days.",
                android.widget.Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                requireContext(),
                "Error generating test data: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Pre-Android 13 doesn't need runtime permission for notifications
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
