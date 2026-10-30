package com.example.localization

/**
 * Complete English & Hindi bilingual localization dictionary for Kharcha Tracker.
 */
object AppStrings {
  const val LANG_EN = "en"
  const val LANG_HI = "hi"

  fun get(key: String, isHindi: Boolean): String {
    return if (isHindi) (hiStrings[key] ?: enStrings[key] ?: key)
    else (enStrings[key] ?: key)
  }

  private val enStrings = mapOf(
    // App Bar & Brand
    "app_name" to "Kharcha Tracker",
    "tagline" to "Smart Daily Expense Manager",
    "welcome_back" to "Welcome Back",
    "currency_symbol" to "₹",

    // Navigation
    "nav_dashboard" to "Dashboard",
    "nav_analytics" to "Analytics",
    "nav_categories" to "Categories",
    "nav_profile" to "Profile",

    // Onboarding
    "onboarding_title_1" to "Track Daily Kharcha",
    "onboarding_desc_1" to "Easily record groceries, milk, fuel, bills, and everyday spending in seconds.",
    "onboarding_title_2" to "Category Sub-Totals",
    "onboarding_desc_2" to "View distinct cards and progress bars for your essential living costs.",
    "onboarding_title_3" to "Bilingual & Private",
    "onboarding_desc_3" to "Full English & Hindi toggle with safe on-device offline storage.",
    "btn_get_started" to "Get Started",
    "btn_skip" to "Skip",
    "btn_next" to "Next",

    // Authentication (Mock OTP)
    "auth_title" to "Quick Mobile Login",
    "auth_subtitle" to "Enter your mobile number to securely access your expense ledger.",
    "phone_label" to "Mobile Number",
    "phone_placeholder" to "e.g. 9876543210",
    "name_label" to "Your Name",
    "name_placeholder" to "e.g. Rahul Sharma",
    "btn_send_otp" to "Get OTP",
    "otp_title" to "Verify Mobile Number",
    "otp_subtitle" to "Enter the 6-digit code sent to",
    "otp_helper" to "Demo OTP is 123456",
    "btn_verify" to "Verify & Continue",
    "btn_resend_otp" to "Resend Code",
    "btn_quick_fill_otp" to "Auto-fill 123456",
    "invalid_phone" to "Please enter a valid 10-digit mobile number",
    "invalid_otp" to "Invalid OTP. Use 123456 for demo verification.",
    "error_fill_name" to "Please enter your name",

    // Dashboard
    "total_expenses" to "Total Expenses",
    "today_expenses" to "Today's Spent",
    "daily_average" to "Daily Average",
    "total_transactions" to "Total Entries",
    "filter_today" to "Today",
    "filter_week" to "This Week",
    "filter_month" to "This Month",
    "filter_all" to "All Time",
    "category_subtotals" to "Category Breakdown",
    "recent_transactions" to "Recent Transactions",
    "see_all" to "See All",
    "no_expenses_title" to "No expenses recorded yet",
    "no_expenses_desc" to "Tap the + button below to log your first expense!",
    "btn_add_expense" to "Add Expense",

    // Add / Edit Expense
    "title_add_expense" to "Add New Expense",
    "title_edit_expense" to "Edit Expense",
    "amount_label" to "Amount",
    "enter_amount" to "Enter amount",
    "quick_amounts" to "Quick Select",
    "category_label" to "Category",
    "select_category" to "Select Category",
    "date_label" to "Date & Day",
    "auto_detected" to "Auto-detected",
    "change_date" to "Change Date",
    "notes_label" to "Notes / Description",
    "notes_placeholder" to "e.g. 2L cow milk, grocery items, electric bill",
    "payment_mode" to "Payment Mode",
    "mode_upi" to "UPI (GPay/PhonePe)",
    "mode_cash" to "Cash",
    "mode_card" to "Card",
    "mode_netbanking" to "Net Banking",
    "btn_save_expense" to "Save Expense",
    "btn_update_expense" to "Update Expense",
    "btn_delete" to "Delete",
    "confirm_delete_title" to "Delete Expense?",
    "confirm_delete_msg" to "Are you sure you want to delete this expense record?",
    "cancel" to "Cancel",
    "save" to "Save",

    // Predefined Categories
    "cat_grocery" to "Grocery (Kirana)",
    "cat_milk" to "Milk",
    "cat_petrol" to "Petrol / Diesel",
    "cat_bills" to "Bills (Phone, Power, Water)",
    "cat_other" to "Other Expenses",

    // Custom Categories
    "cat_food" to "Food & Dining",
    "cat_health" to "Medicine & Health",
    "cat_shopping" to "Clothes & Shopping",
    "cat_transport" to "Auto & Travel",
    "cat_education" to "Education & Books",
    "cat_entertainment" to "Entertainment & Movies",
    "add_custom_category" to "Add Custom Category",
    "cat_name_en" to "Category Name (English)",
    "cat_name_hi" to "Category Name (Hindi)",
    "choose_icon" to "Choose Icon",
    "choose_color" to "Choose Color",
    "btn_create_category" to "Create Category",

    // Analytics & Filtering
    "analytics_title" to "Expense Analytics",
    "analytics_subtitle" to "Gain insights into where your money goes",
    "time_filter" to "Time Filter",
    "tab_month" to "Month-wise",
    "tab_quarter" to "Quarter-wise",
    "tab_year" to "Year-wise",
    "tab_custom" to "Custom Range",
    "select_month" to "Select Month",
    "select_quarter" to "Select Quarter",
    "select_year" to "Select Year",
    "from_date" to "From Date",
    "to_date" to "To Date",
    "q1" to "Q1 (Jan - Mar)",
    "q2" to "Q2 (Apr - Jun)",
    "q3" to "Q3 (Jul - Sep)",
    "q4" to "Q4 (Oct - Dec)",
    "chart_spending_distribution" to "Spending Distribution",
    "chart_category_share" to "Category Share",
    "highest_spending_category" to "Top Category",
    "percentage_of_total" to "of total spent",
    "no_analytics_data" to "No expenses found for this time period.",

    // Profile & Settings
    "profile_title" to "My Profile",
    "logged_in_as" to "Logged in as",
    "language_settings" to "Language (भाषा)",
    "lang_english" to "English",
    "lang_hindi" to "हिंदी (Hindi)",
    "data_storage" to "Local Data Storage",
    "data_storage_desc" to "All entries are securely stored on your device.",
    "clear_all_data" to "Reset All Data",
    "confirm_reset_title" to "Reset All Data?",
    "confirm_reset_msg" to "This will erase all stored expenses and reset custom categories to default.",
    "btn_logout" to "Sign Out",
    "app_version" to "Version 1.0.0 • Made with ❤️ for India"
  )

  private val hiStrings = mapOf(
    // App Bar & Brand
    "app_name" to "खर्चा ट्रैकर",
    "tagline" to "दैनिक खर्चों का स्मार्ट हिसाब",
    "welcome_back" to "नमस्ते",
    "currency_symbol" to "₹",

    // Navigation
    "nav_dashboard" to "डैशबोर्ड",
    "nav_analytics" to "विश्लेषण",
    "nav_categories" to "श्रेणियां",
    "nav_profile" to "प्रोफ़ाइल",

    // Onboarding
    "onboarding_title_1" to "दैनिक खर्चे का आसान हिसाब",
    "onboarding_desc_1" to "दूध, राशन, पेट्रोल, बिल और दैनिक खर्चों को चुटकियों में दर्ज करें।",
    "onboarding_title_2" to "श्रेणी-वार कुल योग",
    "onboarding_desc_2" to "प्रत्येक श्रेणी के लिए अलग कार्ड और प्रतिशत बार देखें।",
    "onboarding_title_3" to "द्विभाषी एवं सुरक्षित",
    "onboarding_desc_3" to "हिंदी और अंग्रेजी में तुरंत बदलें, सुरक्षित ऑफलाइन स्टोरेज के साथ।",
    "btn_get_started" to "शुरू करें",
    "btn_skip" to "छोड़ें",
    "btn_next" to "आगे बढ़ें",

    // Authentication (Mock OTP)
    "auth_title" to "त्वरित मोबाइल लॉगिन",
    "auth_subtitle" to "अपने खर्चों का बहीखाता देखने के लिए अपना मोबाइल नंबर दर्ज करें।",
    "phone_label" to "मोबाइल नंबर",
    "phone_placeholder" to "उदा. 9876543210",
    "name_label" to "आपका नाम",
    "name_placeholder" to "उदा. राहुल शर्मा",
    "btn_send_otp" to "ओटीपी प्राप्त करें",
    "otp_title" to "नंबर सत्यापित करें",
    "otp_subtitle" to "इस नंबर पर भेजा गया 6-अंकीय कोड दर्ज करें",
    "otp_helper" to "डेमो ओटीपी 123456 है",
    "btn_verify" to "सत्यापित करें और आगे बढ़ें",
    "btn_resend_otp" to "ओटीपी दोबारा भेजें",
    "btn_quick_fill_otp" to "123456 भरें",
    "invalid_phone" to "कृपया सही 10-अंकों का मोबाइल नंबर दर्ज करें",
    "invalid_otp" to "गलत ओटीपी। डेमो के लिए 123456 उपयोग करें।",
    "error_fill_name" to "कृपया अपना नाम दर्ज करें",

    // Dashboard
    "total_expenses" to "कुल खर्चे",
    "today_expenses" to "आज का खर्चा",
    "daily_average" to "दैनिक औसत",
    "total_transactions" to "कुल प्रविष्टियां",
    "filter_today" to "आज",
    "filter_week" to "इस सप्ताह",
    "filter_month" to "इस महीने",
    "filter_all" to "सब समय",
    "category_subtotals" to "श्रेणी वार उप-योग",
    "recent_transactions" to "हालिया खर्चे",
    "see_all" to "सभी देखें",
    "no_expenses_title" to "अभी कोई खर्चा दर्ज नहीं है",
    "no_expenses_desc" to "अपना पहला खर्चा जोड़ने के लिए नीचे दिए गए + बटन को दबाएं!",
    "btn_add_expense" to "खर्चा जोड़ें",

    // Add / Edit Expense
    "title_add_expense" to "नया खर्चा जोड़ें",
    "title_edit_expense" to "खर्चा संपादित करें",
    "amount_label" to "राशि",
    "enter_amount" to "राशि दर्ज करें",
    "quick_amounts" to "त्वरित चयन",
    "category_label" to "श्रेणी",
    "select_category" to "श्रेणी चुनें",
    "date_label" to "तारीख और दिन",
    "auto_detected" to "स्वतः पहचाना गया",
    "change_date" to "तारीख बदलें",
    "notes_label" to "नोट्स / विवरण",
    "notes_placeholder" to "उदा. 2L गाय का दूध, किराना सामान, बिजली का बिल",
    "payment_mode" to "भुगतान का माध्यम",
    "mode_upi" to "यूपीआई (UPI)",
    "mode_cash" to "नकद (Cash)",
    "mode_card" to "कार्ड (Card)",
    "mode_netbanking" to "नेट बैंकिंग",
    "btn_save_expense" to "खर्चा सहेजें",
    "btn_update_expense" to "खर्चा अपडेट करें",
    "btn_delete" to "हटाएं",
    "confirm_delete_title" to "खर्चा हटाएं?",
    "confirm_delete_msg" to "क्या आप वाकई इस खर्चे के रिकॉर्ड को हटाना चाहते हैं?",
    "cancel" to "रद्द करें",
    "save" to "सहेजें",

    // Predefined Categories
    "cat_grocery" to "अनाज / किराना",
    "cat_milk" to "दूध",
    "cat_petrol" to "पेट्रोल / डीज़ल",
    "cat_bills" to "बिल्स (फोन, बिजली, पानी)",
    "cat_other" to "अन्य खर्चे",

    // Custom Categories
    "cat_food" to "खाना और होटल",
    "cat_health" to "दवा और स्वास्थ्य",
    "cat_shopping" to "कपड़े व खरीदारी",
    "cat_transport" to "ऑटो व यात्रा",
    "cat_education" to "शिक्षा व पुस्तकें",
    "cat_entertainment" to "मनोरंजन व सिनेमा",
    "add_custom_category" to "नई श्रेणी जोड़ें",
    "cat_name_en" to "श्रेणी का नाम (अंग्रेजी)",
    "cat_name_hi" to "श्रेणी का नाम (हिंदी)",
    "choose_icon" to "प्रतीक / आइकन चुनें",
    "choose_color" to "रंग चुनें",
    "btn_create_category" to "श्रेणी बनाएं",

    // Analytics & Filtering
    "analytics_title" to "खर्चों का विश्लेषण",
    "analytics_subtitle" to "जानें कि आपके पैसे कहाँ खर्च हो रहे हैं",
    "time_filter" to "समय अनुसार फिल्टर",
    "tab_month" to "माह-वार",
    "tab_quarter" to "तिमाही-वार",
    "tab_year" to "वर्ष-वार",
    "tab_custom" to "कस्टम अवधि",
    "select_month" to "महीना चुनें",
    "select_quarter" to "तिमाही चुनें",
    "select_year" to "साल चुनें",
    "from_date" to "प्रारंभ तिथि",
    "to_date" to "अंतिम तिथि",
    "q1" to "Q1 (जनवरी - मार्च)",
    "q2" to "Q2 (अप्रैल - जून)",
    "q3" to "Q3 (जुलाई - सितंबर)",
    "q4" to "Q4 (अक्टूबर - दिसंबर)",
    "chart_spending_distribution" to "खर्च का वितरण",
    "chart_category_share" to "श्रेणी-वार हिस्सेदारी",
    "highest_spending_category" to "शीर्ष श्रेणी",
    "percentage_of_total" to "कुल खर्च का",
    "no_analytics_data" to "इस समयावधि के लिए कोई खर्चा नहीं मिला।",

    // Profile & Settings
    "profile_title" to "मेरी प्रोफ़ाइल",
    "logged_in_as" to "लॉगिन उपयोगकर्ता",
    "language_settings" to "भाषा (Language)",
    "lang_english" to "English (अंग्रेज़ी)",
    "lang_hindi" to "हिंदी (Hindi)",
    "data_storage" to "सुरक्षित लोकल स्टोरेज",
    "data_storage_desc" to "आपका सारा डेटा आपके फोन में सुरक्षित है।",
    "clear_all_data" to "सारा डेटा रीसेट करें",
    "confirm_reset_title" to "डेटा रीसेट करें?",
    "confirm_reset_msg" to "इससे सभी खर्च मिट जाएंगे और डिफ़ॉल्ट श्रेणियां बहाल हो जाएंगी।",
    "btn_logout" to "लॉगआउट करें",
    "app_version" to "संस्करण 1.0.0 • भारत के लिए सप्रेम निर्मित ❤️"
  )
}
