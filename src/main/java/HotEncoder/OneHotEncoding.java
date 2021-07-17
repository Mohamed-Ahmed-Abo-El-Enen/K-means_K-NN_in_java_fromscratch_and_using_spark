package HotEncoder;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.OneHotEncoderModel;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class OneHotEncoding
{
    public static Dataset<Row> GetOneHotEncoding(SparkSession sparkSession, Dataset<Row> data, String[] columnsName)
    {
        String[] inputColumnsIndex = new String[columnsName.length];
        for(int i=0; i<columnsName.length; i++)
            inputColumnsIndex[i] = columnsName[i] + "Index";

        StringIndexerModel indexer = new StringIndexer()
                .setInputCols(columnsName)
                .setOutputCols(inputColumnsIndex)
                .fit(data);

        Dataset<Row> indexed_df = indexer.transform(data);
        indexed_df.show();

        String[] outputColumnsVec = new String[inputColumnsIndex.length];
        for(int i=0; i<inputColumnsIndex.length; i++)
            outputColumnsVec[i] = columnsName[i] + "EncoderVec";

        OneHotEncoder encoder = new OneHotEncoder()
                .setInputCols(inputColumnsIndex)
                .setOutputCols(outputColumnsVec);

        OneHotEncoderModel model = encoder.fit(indexed_df);
        Dataset<Row> encoded = model.transform(indexed_df);
        encoded.show();
        return encoded;
    }
}