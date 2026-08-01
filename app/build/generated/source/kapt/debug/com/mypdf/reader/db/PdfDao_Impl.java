package com.mypdf.reader.db;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Integer;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class PdfDao_Impl implements PdfDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<PdfEntity> __insertAdapterOfPdfEntity;

  public PdfDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfPdfEntity = new EntityInsertAdapter<PdfEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_list` (`path`,`name`,`isRead`,`position`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final PdfEntity entity) {
        if (entity.getPath() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getPath());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        final int _tmp = entity.isRead() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.getPosition());
      }
    };
  }

  @Override
  public void insert(final PdfEntity entity) {
    if (entity == null) throw new NullPointerException();
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfPdfEntity.insert(_connection, entity);
      return null;
    });
  }

  @Override
  public void insertAll(final List<PdfEntity> entities) {
    if (entities == null) throw new NullPointerException();
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfPdfEntity.insert(_connection, entities);
      return null;
    });
  }

  @Override
  public List<PdfEntity> getAll() {
    final String _sql = "SELECT * FROM reading_list ORDER BY position ASC";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "path");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfIsRead = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isRead");
        final int _columnIndexOfPosition = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "position");
        final List<PdfEntity> _result = new ArrayList<PdfEntity>();
        while (_stmt.step()) {
          final PdfEntity _item;
          final String _tmpPath;
          if (_stmt.isNull(_columnIndexOfPath)) {
            _tmpPath = null;
          } else {
            _tmpPath = _stmt.getText(_columnIndexOfPath);
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final boolean _tmpIsRead;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsRead));
          _tmpIsRead = _tmp != 0;
          final int _tmpPosition;
          _tmpPosition = (int) (_stmt.getLong(_columnIndexOfPosition));
          _item = new PdfEntity(_tmpPath,_tmpName,_tmpIsRead,_tmpPosition);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Integer getMaxPosition() {
    final String _sql = "SELECT MAX(position) FROM reading_list";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void updateReadStatus(final String path, final boolean isRead) {
    final String _sql = "UPDATE reading_list SET isRead = ? WHERE path = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        final int _tmp = isRead ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, path);
        }
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteByPath(final String path) {
    final String _sql = "DELETE FROM reading_list WHERE path = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, path);
        }
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteAll() {
    final String _sql = "DELETE FROM reading_list";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
