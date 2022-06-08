package amn.inventory.helpers;

public class DatabaseStructure {

    public static class DataTable {

        public static final String tableName = "data";

        public static class Columns {
            public static final String _id = "_id";
            public static final String search_id = "search_id";
            public static final String title = "title";
            public static final String quantity = "quantity";
            public static final String scanned_quantity = "scanned_quantity";
            public static final String result = "result";
            public static final String category = "category";
            public static final String description = "description";
        }

    }

    public static class TitlesTable {

        public static final String tableName = "titles";

        public static class Columns {
            public static final String _id = "_id";
            public static final String title = "title";
        }

    }

    public static class CategoriesTable {

        public static final String tableName = "categories";

        public static class Columns {
            public static final String _id = "_id";
            public static final String category_name = "category_name";
            public static final String category_completed = "category_completed";
            public static final String category_wrong = "category_wrong";
            public static final String total_quantity = "total_quantity";
        }

    }
}