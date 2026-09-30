package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PreschoolClass(
    val code: String,
    val displayName: String,
    val subtitle: String,
    val ageGroup: String,
    val defaultTeacher: String,
    val roomName: String
) {
    NURSERY(
        code = "NURSERY",
        displayName = "Nursery",
        subtitle = "Little Sprouts",
        ageGroup = "2.5 – 3.5 Yrs",
        defaultTeacher = "Rainbow Pre-School Supa",
        roomName = "Sunshine Wing • Room 101"
    ),
    LKG(
        code = "LKG",
        displayName = "LKG",
        subtitle = "Curious Explorers",
        ageGroup = "3.5 – 4.5 Yrs",
        defaultTeacher = "Rainbow Pre-School Supa",
        roomName = "Rainbow Wing • Room 102"
    ),
    UKG(
        code = "UKG",
        displayName = "UKG",
        subtitle = "Rising Stars",
        ageGroup = "4.5 – 5.5 Yrs",
        defaultTeacher = "Rainbow Pre-School Supa",
        roomName = "Starlight Wing • Room 103"
    );

    companion object {
        fun fromCode(code: String): PreschoolClass =
            entries.find { it.code == code } ?: NURSERY
    }
}

enum class EntryCategory(
    val code: String,
    val label: String,
    val pluralLabel: String,
    val completionLabel: String
) {
    DAILY_TASK(
        code = "DAILY_TASK",
        label = "Daily Task",
        pluralLabel = "Daily Tasks",
        completionLabel = "Activity Done"
    ),
    HOMEWORK(
        code = "HOMEWORK",
        label = "Homework",
        pluralLabel = "Homework",
        completionLabel = "Homework Done"
    ),
    INSTRUCTION(
        code = "INSTRUCTION",
        label = "Instruction",
        pluralLabel = "Instructions",
        completionLabel = "Noted by Parent"
    );

    companion object {
        fun fromCode(code: String): EntryCategory =
            entries.find { it.code == code } ?: DAILY_TASK
    }
}

@Entity(tableName = "preschool_entries")
data class PreschoolEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classCode: String,
    val categoryCode: String,
    val title: String,
    val description: String,
    val subjectTag: String,
    val scheduleOrDue: String,
    val materialsNeeded: String = "",
    val teacherName: String,
    val isPinned: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val preschoolClass: PreschoolClass
        get() = PreschoolClass.fromCode(classCode)

    val category: EntryCategory
        get() = EntryCategory.fromCode(categoryCode)
}

data class QuickTemplate(
    val category: EntryCategory,
    val title: String,
    val description: String,
    val subjectTag: String,
    val scheduleOrDue: String,
    val materialsNeeded: String,
    val isPinned: Boolean = false
)

object PreschoolSeedData {

    val subjectSuggestions = mapOf(
        EntryCategory.DAILY_TASK to listOf(
            "Circle Time & Rhymes",
            "Sensory & Motor Play",
            "Art & Craft",
            "Storytelling",
            "Phonics Activity",
            "Number Games"
        ),
        EntryCategory.HOMEWORK to listOf(
            "English & Phonics",
            "Maths & Numbers",
            "EVS & General Awareness",
            "Coloring & Tracing",
            "Marathi / Hindi Oral",
            "Parent-Child Activity"
        ),
        EntryCategory.INSTRUCTION to listOf(
            "Important Notice",
            "Dress Code / Color Day",
            "Healthy Tiffin Tip",
            "Event & Celebration",
            "PTM & Timings",
            "Health & Safety"
        )
    )

    fun getTemplatesForClass(preschoolClass: PreschoolClass): List<QuickTemplate> {
        return when (preschoolClass) {
            PreschoolClass.NURSERY -> listOf(
                QuickTemplate(
                    category = EntryCategory.DAILY_TASK,
                    title = "Finger Painting: Rainbow Arch",
                    description = "Children will dip fingers in safe washable tempera paints to fill a big rainbow arch on chart paper while naming Red, Yellow, Blue, and Green.",
                    subjectTag = "Art & Craft",
                    scheduleOrDue = "Morning Session • 10:15 AM",
                    materialsNeeded = "Washable Apron, Drawing Sheet"
                ),
                QuickTemplate(
                    category = EntryCategory.HOMEWORK,
                    title = "Standing & Sleeping Lines Tracing",
                    description = "Help your little one trace the dotted standing lines ( | ) and sleeping lines ( — ) using jumbo crayons. Encourage a gentle 3-finger grip.",
                    subjectTag = "Coloring & Tracing",
                    scheduleOrDue = "Due Tomorrow",
                    materialsNeeded = "Pattern Workbook Pg. 6, Jumbo Crayons"
                ),
                QuickTemplate(
                    category = EntryCategory.INSTRUCTION,
                    title = "Yellow Color Day Celebration on Friday",
                    description = "Please dress your child in any comfortable yellow outfit this Friday. Kindly pack one yellow fruit or snack (banana, mango, or sweet corn) in their tiffin box.",
                    subjectTag = "Dress Code / Color Day",
                    scheduleOrDue = "This Friday • 9:00 AM",
                    materialsNeeded = "Yellow Dress, 1 Yellow Fruit/Snack",
                    isPinned = true
                )
            )
            PreschoolClass.LKG -> listOf(
                QuickTemplate(
                    category = EntryCategory.DAILY_TASK,
                    title = "Phonics Sound Basket (Letters A to H)",
                    description = "Interactive circle-time game matching miniature toy objects (Apple, Ball, Cat, Drum, Elephant, Fish) with their beginning phonics sound cards.",
                    subjectTag = "Phonics Activity",
                    scheduleOrDue = "Today • 10:30 AM",
                    materialsNeeded = "Phonics Flashcards Set 1"
                ),
                QuickTemplate(
                    category = EntryCategory.HOMEWORK,
                    title = "Write Capital Letters E, F, G & Count 1–10",
                    description = "Complete 1 page of capital letters E, F, and G in the 4-line notebook. Also count and color 8 butterflies in the Maths activity book.",
                    subjectTag = "English & Phonics",
                    scheduleOrDue = "Due Tomorrow",
                    materialsNeeded = "4-Line Notebook, Maths Book Pg. 14"
                ),
                QuickTemplate(
                    category = EntryCategory.INSTRUCTION,
                    title = "Label Water Bottles, Raincoats & Tiffin Boxes",
                    description = "Kindly ensure your child's name and class (LKG - Supa) are clearly labeled with waterproof stickers on their water bottle, bag, and tiffin box.",
                    subjectTag = "Important Notice",
                    scheduleOrDue = "Daily Reminder",
                    materialsNeeded = "Name Stickers, Napkin & Spoon in Tiffin",
                    isPinned = true
                )
            )
            PreschoolClass.UKG -> listOf(
                QuickTemplate(
                    category = EntryCategory.DAILY_TASK,
                    title = "CVC Sight Words & Number Line Hop",
                    description = "Students blend 3-letter 'a' and 'e' family words (cat, bat, pen, net) on the magnetic board, followed by forward and backward counting 1 to 50.",
                    subjectTag = "Circle Time & Rhymes",
                    scheduleOrDue = "Today • 11:00 AM",
                    materialsNeeded = "Magnetic Word Kit, Number Mat"
                ),
                QuickTemplate(
                    category = EntryCategory.HOMEWORK,
                    title = "Three-Letter Word Sentences & Addition (1–10)",
                    description = "Read aloud and write 5 three-letter 'a' vowel words in the English notebook. Solve single-digit picture addition sums on page 22.",
                    subjectTag = "Maths & Numbers",
                    scheduleOrDue = "Due Tomorrow",
                    materialsNeeded = "English Notebook, Maths Workbook Pg. 22"
                ),
                QuickTemplate(
                    category = EntryCategory.INSTRUCTION,
                    title = "Show & Tell: 'My Favorite Season' Preparation",
                    description = "Each UKG student will speak 3 to 4 simple sentences about their favorite season next Monday. Parents may send 1 handmade prop or chart.",
                    subjectTag = "Event & Celebration",
                    scheduleOrDue = "Monday • 9:30 AM",
                    materialsNeeded = "1 Simple Prop or Picture Flashcard",
                    isPinned = true
                )
            )
        }
    }

    fun initialEntries(): List<PreschoolEntry> {
        val now = System.currentTimeMillis()
        return listOf(
            // ==================== NURSERY ====================
            PreschoolEntry(
                classCode = PreschoolClass.NURSERY.code,
                categoryCode = EntryCategory.INSTRUCTION.code,
                title = "Yellow Color Day & Fruit Party this Friday",
                description = "Dear Nursery Parents, we are celebrating 'Sunny Yellow Day' at Rainbow Preschool Supa! Please send your child dressed in yellow clothes and include one yellow fruit or snack (like banana slices, sweet corn, or poha) in their tiffin.",
                subjectTag = "Dress Code / Color Day",
                scheduleOrDue = "Friday • 9:00 AM",
                materialsNeeded = "Yellow Outfit, 1 Yellow Fruit in Tiffin, Extra Napkin",
                teacherName = PreschoolClass.NURSERY.defaultTeacher,
                isPinned = true,
                isCompleted = false,
                createdAt = now - 3600_000L * 1
            ),
            PreschoolEntry(
                classCode = PreschoolClass.NURSERY.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Action Rhymes & Clay Ball Rolling",
                description = "1. Morning prayer and welcome song ('Good Morning Sun').\n2. Action rhymes: 'Twinkle Twinkle' & 'Wheels on the Bus'.\n3. Fine-motor play: Rolling soft non-toxic playdough into balls and caterpillars to strengthen finger grip.",
                subjectTag = "Sensory & Motor Play",
                scheduleOrDue = "Today • 9:30 AM – 11:00 AM",
                materialsNeeded = "Playdough Kit (Provided in Class)",
                teacherName = PreschoolClass.NURSERY.defaultTeacher,
                isPinned = false,
                isCompleted = true,
                createdAt = now - 3600_000L * 2
            ),
            PreschoolEntry(
                classCode = PreschoolClass.NURSERY.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Cotton Dabbing on Cloud & Rainbow Cutout",
                description = "Children dab soft cotton balls dipped in sky-blue and pastel colors onto a large cloud cutout while learning color recognition and sharing materials.",
                subjectTag = "Art & Craft",
                scheduleOrDue = "Today • 11:30 AM",
                materialsNeeded = "Scrapbook Pg. 4, Apron",
                teacherName = PreschoolClass.NURSERY.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 3
            ),
            PreschoolEntry(
                classCode = PreschoolClass.NURSERY.code,
                categoryCode = EntryCategory.HOMEWORK.code,
                title = "Trace Standing Lines ( | ) & Color the Red Apple",
                description = "Open Pattern Readiness Book to Page 5. Guide your child to trace the dotted standing lines from top to bottom using a red or blue jumbo crayon, then color inside the apple outline.",
                subjectTag = "Coloring & Tracing",
                scheduleOrDue = "Due Tomorrow",
                materialsNeeded = "Pattern Readiness Book Pg. 5, Jumbo Crayons",
                teacherName = PreschoolClass.NURSERY.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 4
            ),
            PreschoolEntry(
                classCode = PreschoolClass.NURSERY.code,
                categoryCode = EntryCategory.INSTRUCTION.code,
                title = "Spare Set of Clothes in School Bag",
                description = "Kindly keep one labeled pair of comfortable cotton clothes and a small towel in your child's bag every day in case of water or paint spills during sensory activities.",
                subjectTag = "Health & Safety",
                scheduleOrDue = "Daily Checklist",
                materialsNeeded = "1 Labeled Cotton Pair + Small Hand Towel",
                teacherName = PreschoolClass.NURSERY.defaultTeacher,
                isPinned = false,
                isCompleted = true,
                createdAt = now - 3600_000L * 5
            ),

            // ==================== LKG ====================
            PreschoolEntry(
                classCode = PreschoolClass.LKG.code,
                categoryCode = EntryCategory.INSTRUCTION.code,
                title = "Healthy Sprouts & Vegetable Tiffin Week",
                description = "This week LKG is learning about 'Healthy Foods & Farm Vegetables'. Please avoid biscuits or packaged chips in the tiffin box and include paratha, idli, poha, or colourful cucumber/carrot sticks.",
                subjectTag = "Healthy Tiffin Tip",
                scheduleOrDue = "All Week • Tiffin Time 10:45 AM",
                materialsNeeded = "Nutritious Homemade Tiffin, Table Mat, Spoon",
                teacherName = PreschoolClass.LKG.defaultTeacher,
                isPinned = true,
                isCompleted = false,
                createdAt = now - 3600_000L * 1
            ),
            PreschoolEntry(
                classCode = PreschoolClass.LKG.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Phonics Sounds A–H & Sandpaper Letter Tracing",
                description = "Children practiced phonetic sounds from /a/ to /h/ with picture flashcards and traced textured sandpaper letters 'G' (Grapes, Goat) and 'H' (House, Hat).",
                subjectTag = "Phonics Activity",
                scheduleOrDue = "Today • 9:45 AM",
                materialsNeeded = "My First Phonics Reader Pg. 10",
                teacherName = PreschoolClass.LKG.defaultTeacher,
                isPinned = false,
                isCompleted = true,
                createdAt = now - 3600_000L * 2
            ),
            PreschoolEntry(
                classCode = PreschoolClass.LKG.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Count & Peg Beads (Numbers 1 to 10)",
                description = "Hands-on counting activity where children clip the matching number of colorful wooden pegs onto number cards 1 through 10.",
                subjectTag = "Number Games",
                scheduleOrDue = "Today • 11:15 AM",
                materialsNeeded = "Classroom Montessori Number Kit",
                teacherName = PreschoolClass.LKG.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 3
            ),
            PreschoolEntry(
                classCode = PreschoolClass.LKG.code,
                categoryCode = EntryCategory.HOMEWORK.code,
                title = "Write Letters 'G' and 'H' + Count & Match (1–10)",
                description = "1. English: Write capital letters G and H neatly in the 4-line red-and-blue notebook (1 page).\n2. Maths: Count the objects and match them with the correct number on Workbook Page 15.",
                subjectTag = "English & Phonics",
                scheduleOrDue = "Due Tomorrow",
                materialsNeeded = "4-Line Notebook, Maths Wonder Book Pg. 15, HB Pencil",
                teacherName = PreschoolClass.LKG.defaultTeacher,
                isPinned = true,
                isCompleted = false,
                createdAt = now - 3600_000L * 4
            ),
            PreschoolEntry(
                classCode = PreschoolClass.LKG.code,
                categoryCode = EntryCategory.HOMEWORK.code,
                title = "Oral Practice: 5 Domestic Animals & Their Homes",
                description = "Revise the names of 5 domestic animals (Cow, Dog, Horse, Sheep, Hen) and where they live using the EVS Picture Dictionary Page 8.",
                subjectTag = "EVS & General Awareness",
                scheduleOrDue = "Due Thursday",
                materialsNeeded = "EVS Picture Dictionary Pg. 8",
                teacherName = PreschoolClass.LKG.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 5
            ),

            // ==================== UKG ====================
            PreschoolEntry(
                classCode = PreschoolClass.UKG.code,
                categoryCode = EntryCategory.INSTRUCTION.code,
                title = "Parent-Teacher Interaction & Work Folder Review",
                description = "Dear UKG Parents, monthly activity folders and assessment worksheets will be sent home this Saturday. Kindly sign the progress sheet and return the folder on Monday morning.",
                subjectTag = "PTM & Timings",
                scheduleOrDue = "Saturday • 9:30 AM – 11:30 AM",
                materialsNeeded = "Student Diary & Progress Folder",
                teacherName = PreschoolClass.UKG.defaultTeacher,
                isPinned = true,
                isCompleted = false,
                createdAt = now - 3600_000L * 1
            ),
            PreschoolEntry(
                classCode = PreschoolClass.UKG.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Vowel 'a' & 'e' Word Blending + Story Circle",
                description = "Students blended 3-letter CVC words (-at, -an, -en, -et families) and read short sentences aloud from the board, followed by an interactive moral story 'The Clever Rabbit'.",
                subjectTag = "Circle Time & Rhymes",
                scheduleOrDue = "Today • 9:30 AM",
                materialsNeeded = "Phonics Step-Up Reader Pg. 18",
                teacherName = PreschoolClass.UKG.defaultTeacher,
                isPinned = false,
                isCompleted = true,
                createdAt = now - 3600_000L * 2
            ),
            PreschoolEntry(
                classCode = PreschoolClass.UKG.code,
                categoryCode = EntryCategory.DAILY_TASK.code,
                title = "Before, After & Between Numbers (1 to 50)",
                description = "Floor number-line jump game where students identify what comes just before, after, and between numbers up to 50.",
                subjectTag = "Number Games",
                scheduleOrDue = "Today • 11:00 AM",
                materialsNeeded = "Square-Line Maths Notebook",
                teacherName = PreschoolClass.UKG.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 3
            ),
            PreschoolEntry(
                classCode = PreschoolClass.UKG.code,
                categoryCode = EntryCategory.HOMEWORK.code,
                title = "Write 10 '-en' & '-et' Family Words + Number Names 1–10",
                description = "1. English: Write and read aloud 5 '-en' words (pen, hen, ten, men, den) and 5 '-et' words (net, pet, jet, wet, vet).\n2. Maths: Write number names ONE to TEN in the square-line notebook.",
                subjectTag = "Maths & Numbers",
                scheduleOrDue = "Due Tomorrow",
                materialsNeeded = "4-Line English Book, Square-Line Maths Book",
                teacherName = PreschoolClass.UKG.defaultTeacher,
                isPinned = true,
                isCompleted = false,
                createdAt = now - 3600_000L * 4
            ),
            PreschoolEntry(
                classCode = PreschoolClass.UKG.code,
                categoryCode = EntryCategory.HOMEWORK.code,
                title = "Origami Paper Boat & Rainy Season Drawing",
                description = "Fold a simple colored glaze paper boat with your child, paste it on page 9 of the Art book, and draw clouds and raindrops around it.",
                subjectTag = "Parent-Child Activity",
                scheduleOrDue = "Due Friday",
                materialsNeeded = "Art & Craft Book Pg. 9, 1 Glaze Paper, Glue Stick",
                teacherName = PreschoolClass.UKG.defaultTeacher,
                isPinned = false,
                isCompleted = false,
                createdAt = now - 3600_000L * 5
            )
        )
    }
}
