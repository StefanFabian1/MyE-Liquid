package sk.sfabian.myeliquid.repository.room

import androidx.room.TypeConverter
import org.bson.types.ObjectId

class ObjectIdConverter {
    @TypeConverter
    fun fromString(value: String?): ObjectId? {
        return value?.let { ObjectId(it) }
    }

    @TypeConverter
    fun toString(objectId: ObjectId?): String? {
        return objectId?.toHexString()
    }
}