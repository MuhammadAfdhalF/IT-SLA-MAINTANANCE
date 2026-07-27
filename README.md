# 🔧 IT SLA Maintenance – Mobile Application

A mobile-based internal IT maintenance application developed for the IT divisions of **PT Makassar Metro Network** and **PT Makassar Airport Network**, part of **PT Nusantara Infrastructure Tbk**.

The application helps users and technicians submit maintenance reports, monitor ticket progress, track Service Level Agreement performance, and review maintenance histories directly from Android devices.

---

## 📌 About the Project

**IT SLA Maintenance Mobile Application** was developed to support internal IT maintenance operations in a more efficient, transparent, and measurable way.

Through this application, users can report IT equipment problems by providing descriptions, photos, and other supporting information. Technicians can access assigned maintenance tickets, update work progress, record maintenance activities, and review previous work histories.

The application is connected to a centralized web administration system through a RESTful API. This allows maintenance data, ticket statuses, technician activities, and inventory information to remain synchronized between the mobile application and the administration website.

The system supports the IT division in managing:

* IT equipment maintenance
* Maintenance ticket reporting
* Technician work assignments
* Equipment maintenance history
* Service Level Agreement monitoring
* Technician response and resolution times

---

## 🛠️ Technologies

* 📱 **Kotlin** → Android application development
* 🔗 **Retrofit** → RESTful API communication
* 🌐 **Laravel REST API** → backend service and data processing
* 📄 **JSON** → data exchange format
* 🗄️ **SQL Database** → maintenance, user, ticket, and history storage
* 🖥️ **Admin Website** → inventory management and SLA monitoring

---

## 🌐 System Architecture

### Mobile Application

The mobile application is used by users and technicians to access maintenance services from Android devices.

Main responsibilities:

* Submit maintenance reports
* Upload maintenance photos
* Add maintenance descriptions and notes
* View maintenance tickets
* Track ticket status
* View assigned technician work
* Update maintenance progress
* Review maintenance history

---

### API Layer

The API connects the Android application, administration website, and database.

Main responsibilities:

* Provide RESTful endpoints
* Process authentication requests
* Receive maintenance reports
* Synchronize ticket information
* Update maintenance status
* Process technician activity data
* Exchange data using JSON
* Connect the mobile application with the web administration system

---

### Admin Website

The administration website is used by administrators and IT personnel to manage and monitor maintenance activities.

Main responsibilities:

* Review maintenance reports
* Assign technicians
* Monitor technician working hours
* Manage IT inventory
* Manage maintenance schedules
* Review maintenance histories
* Monitor SLA performance
* Generate maintenance and technician reports

Website repository:

[IT SLA Maintenance Website](https://github.com/MuhammadAfdhalF/IT-SLA-Maintenance-Website)

---

## ⚙️ Main Features

* 📥 **Maintenance Ticket Submission**
  Users can submit IT maintenance reports with photos, descriptions, and supporting notes.

* 📋 **Ticket Management**
  Users and technicians can view maintenance ticket information and current ticket status.

* ⏱️ **SLA Tracking**
  The system records response time, work duration, and resolution time for maintenance activities.

* 👨‍🔧 **Technician Work Management**
  Technicians can view assigned maintenance work and update activity progress.

* 📸 **Photo Documentation**
  Maintenance reports can include photos to provide clearer information about equipment problems.

* 🔄 **Status Updates**
  Ticket statuses can be updated throughout the maintenance process.

* 📜 **Maintenance History**
  Users and technicians can review previous maintenance activities and completed work.

* 📦 **Inventory Integration**
  Maintenance activities can be connected to IT equipment and inventory records managed through the administration website.

* 🔔 **Ticket Notifications**
  Users can receive maintenance status updates and SLA-related information.

* 📊 **SLA Performance Monitoring**
  Maintenance data can be used to evaluate technician response and resolution performance.

* 🌐 **Web and Mobile Synchronization**
  Maintenance information is synchronized between the Android application and the administration website through a RESTful API.

---

## 🔄 Main Application Flow

```text
User logs in to the mobile application
↓
User submits a maintenance report
↓
User adds a problem description, photo, and notes
↓
The report is sent to the Laravel REST API
↓
The ticket appears on the administration website
↓
Administrator reviews the maintenance report
↓
Administrator assigns a technician
↓
Technician views the assigned ticket
↓
Technician starts the maintenance work
↓
Technician updates the work progress
↓
The maintenance activity is completed
↓
Ticket status and maintenance history are updated
↓
SLA performance can be reviewed through the administration website
```

---

## 👥 User Roles

### 👤 User

Users can:

* Log in to the mobile application
* Submit maintenance reports
* Add maintenance photos
* Add problem descriptions and notes
* View submitted tickets
* Track maintenance status
* View maintenance progress
* Review maintenance history
* Receive ticket status information

---

### 👨‍🔧 Technician

Technicians can:

* Log in to the mobile application
* View assigned maintenance tickets
* View maintenance details
* Start maintenance work
* Update work progress
* Add maintenance notes
* Complete assigned maintenance activities
* Review previous maintenance work
* Monitor assigned ticket statuses

---

### 👨‍💼 Administrator

Administrators primarily use the connected administration website to:

* Review submitted maintenance tickets
* Assign technicians
* Monitor maintenance progress
* Manage IT equipment
* Manage inventory
* Monitor technician working hours
* Review SLA performance
* Manage maintenance schedules
* View reports and maintenance histories

---

## ⏱️ SLA Monitoring

The application supports Service Level Agreement monitoring by recording important maintenance timelines, including:

* Ticket submission time
* Technician assignment time
* Technician response time
* Maintenance start time
* Work completion time
* Resolution time
* Total technician work duration
* Maintenance status history

This information helps the IT division evaluate maintenance performance more objectively and identify opportunities for operational improvement.

---

## 📈 Impact

✅ **Improved Operational Efficiency**
Maintenance reports and technician updates can be processed more quickly through mobile devices.

✅ **Accurate Maintenance Records**
Ticket information, maintenance activities, and work histories are stored digitally.

✅ **Transparent Work Progress**
Users and administrators can monitor maintenance progress from the initial report until completion.

✅ **Measurable SLA Performance**
Technician response time, work duration, and resolution time can be evaluated using recorded data.

✅ **Better Technician Coordination**
Technicians can access assigned maintenance work directly from the mobile application.

✅ **Centralized Data Management**
Maintenance data is synchronized with the administration website and stored in a centralized database.

✅ **Improved Maintenance Documentation**
Photos, notes, ticket statuses, and maintenance histories provide clearer documentation for each activity.

---

## 🔗 Related Repository

The web administration system connected to this mobile application is available at:

[IT SLA Maintenance Website](https://github.com/MuhammadAfdhalF/IT-SLA-Maintenance-Website)

```text
Mobile Application : Kotlin
API Client         : Retrofit
Backend            : Laravel REST API
Data Format        : JSON
Database           : SQL Database
Admin System       : React Web Application
```

---

## 🙋‍♂️ Author

**Muhammad Afdhal F**

📧 Email: [cuyafdal@gmail.com](mailto:muhammad.afdhal.f01@gmail.com)
📷 Instagram: [@holla.cuy](https://instagram.com/holla.cuy)
💼 LinkedIn: [Muhammad Afdhal F](https://id.linkedin.com/in/muhammad-afdhal-f-3b3317217)
💻 GitHub: [MuhammadAfdhalF](https://github.com/MuhammadAfdhalF)

🧠 Passionate about Mobile Development, Web Development, QA Automation, and Artificial Intelligence.

---

Developed for the internal IT operations of **PT Makassar Metro Network** and **PT Makassar Airport Network**, part of **PT Nusantara Infrastructure Tbk**.
