/* 
 * Copyright (C) 2010---2014 星星(wuweixing)<349446658@qq.com>
 * 
 * This file is part of Wabacus 
 * 
 * Wabacus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wabacus.config.database.type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wabacus.exception.WabacusRuntimeException;
import com.wabacus.system.dataset.select.report.value.SQLReportDataSetValueProvider;
import com.wabacus.system.dataset.select.report.value.sqlconvertor.AbsConvertSQLevel;
import com.wabacus.system.datatype.BigdecimalType;
import com.wabacus.system.datatype.BlobType;
import com.wabacus.system.datatype.ByteType;
import com.wabacus.system.datatype.ClobType;
import com.wabacus.system.datatype.DateType;
import com.wabacus.system.datatype.DoubleType;
import com.wabacus.system.datatype.FloatType;
import com.wabacus.system.datatype.IDataType;
import com.wabacus.system.datatype.IntType;
import com.wabacus.system.datatype.LongType;
import com.wabacus.system.datatype.ShortType;
import com.wabacus.system.datatype.TimeType;
import com.wabacus.system.datatype.TimestampType;
import com.wabacus.system.datatype.VarcharType;
import com.wabacus.util.Tools;

public class MySql extends AbsDatabaseType
{
    private final static Log log=LogFactory.getLog(MySql.class);

    public String constructSplitPageSql(AbsConvertSQLevel convertSqlObj)
    {
        String sql=convertSqlObj.getConvertedSql();
        if(sql.indexOf(SQLReportDataSetValueProvider.orderbyPlaceHolder)>0)
        {
            sql=Tools.replaceAll(sql,SQLReportDataSetValueProvider.orderbyPlaceHolder," order by "+convertSqlObj.getOrderby());
        }
        //            throw new ReportFilesLoadingException("配置的sql语句：" + sql + "不合法，左右括号没有配对");
        //        {
        //            alrsbean.setSql(sql.substring(0,idxOrd) + " %orderby% " + sql.substring(idxLi));
        sql=sql+" limit "+SQLReportDataSetValueProvider.startRowNumPlaceHolder+","+SQLReportDataSetValueProvider.pagesizePlaceHolder;
        return sql;
    }

    public String constructSplitPageSql(AbsConvertSQLevel convertSqlObj,String dynorderby)
    {
        dynorderby=convertSqlObj.mixDynorderbyAndConfigOrderbyCols(dynorderby);
        dynorderby=" order by "+dynorderby;
        String sql=convertSqlObj.getConvertedSql();
        if(sql.indexOf(SQLReportDataSetValueProvider.orderbyPlaceHolder)<0)
        {
            sql=sql+dynorderby;
        }else
        {
            sql=Tools.replaceAll(sql,SQLReportDataSetValueProvider.orderbyPlaceHolder,dynorderby);
        }
        sql=sql+"  limit "+SQLReportDataSetValueProvider.startRowNumPlaceHolder+","+SQLReportDataSetValueProvider.pagesizePlaceHolder;
        return sql;
    }

    public String getSequenceValueByName(String sequencename)
    {
        log.warn("MySql数据库不支持序列（sequence）的配置，只有支持sequence的数据库才支持从序列中取值，比如Oracle、DB2等");
        return "";
    }

    public String getSequenceValueSql(String sequencename)
    {
       throw new WabacusRuntimeException("MySql数据库不支持序列");
    }
    
    public IDataType getWabacusDataTypeByColumnType(String columntype)
    {
        if(columntype==null||columntype.trim().equals("")) return null;
        columntype=columntype.toLowerCase().trim();
        IDataType dataTypeObj=null;
        if(columntype.equals("varchar")||columntype.equals("char"))
        {
            dataTypeObj=new VarcharType();
        }else if(columntype.equals("tinyint"))
        {
            dataTypeObj=new ByteType();
        }else if(columntype.equals("smallint"))
        {
            dataTypeObj=new ShortType();
        }else if(columntype.equals("int")||columntype.equals("integer"))
        {
            dataTypeObj=new IntType();
        }else if(columntype.equals("bigint"))
        {
            dataTypeObj=new LongType();
        }else if(columntype.equals("tinyblob")||columntype.equals("mediumblob")||columntype.equals("blob")||columntype.equals("longblob"))
        {
            dataTypeObj=new BlobType();
        }else if(columntype.equals("date")||columntype.equals("datetime")||columntype.equals("year"))
        {
            dataTypeObj=new DateType();
        }else if(columntype.equals("time"))
        {
            dataTypeObj=new TimeType();
        }else if(columntype.equals("decimal"))
        {
            dataTypeObj=new BigdecimalType();
        }else if(columntype.equals("float"))
        {
            dataTypeObj=new FloatType();
        }else if(columntype.equals("double"))
        {
            dataTypeObj=new DoubleType();
        }else if(columntype.equals("timestamp"))
        {
            dataTypeObj=new TimestampType();
        }else if(columntype.equals("text")||columntype.equals("tinytext")||columntype.equals("mediumtext")||columntype.equals("longtext"))
        {
            dataTypeObj=new ClobType();
        }else
        {
            log.warn("数据类型："+columntype+"不支持，将当做varchar类型");
            dataTypeObj=new VarcharType();
        }
        return dataTypeObj;
    }
}
