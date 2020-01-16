package com.collins.trustedsystems.briefcase.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonUtil<T> {

	private static class ListParameterizedType implements ParameterizedType {

		private Type type;

		private ListParameterizedType(Type type) {
			this.type = type;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { type };
		}

		@Override
		public Type getRawType() {
			return ArrayList.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}

	}

	private final Class<T> cls;

	public JsonUtil(Class<T> c) {
		this.cls = c;
	}

	public List<T> readList(File file) throws Exception {
		try {
			Gson gson = new Gson();
			JsonReader jsonReader = new JsonReader(new FileReader(file));
			List<T> data = gson.fromJson(jsonReader, new ListParameterizedType(this.cls));
			jsonReader.close();
			return data;
		} catch (Exception e) {
			throw e;
		}
	}

	public T readObject(File file) throws Exception {
		try {
//			Gson gson = new Gson();
			Gson gson = new GsonBuilder()
					.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE).create();
			JsonReader jsonReader = new JsonReader(new FileReader(file));
			T data = gson.fromJson(jsonReader, this.cls);
			jsonReader.close();
			return data;
		} catch (Exception e) {
			throw e;
		}
	}

	public void writeList(T data, File file) throws Exception {
		try {
			Gson gson = new Gson();
			JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
			jsonWriter.setIndent("    ");
			gson.toJson(data, new ListParameterizedType(this.cls), jsonWriter);
			jsonWriter.close();
		} catch (Exception e) {
			throw e;
		}
	}

	public void writeObject(T data, File file) throws Exception {
		try {
			Gson gson = new Gson();
			JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
			jsonWriter.setIndent("    ");
			gson.toJson(data, this.cls, jsonWriter);
			jsonWriter.close();
		} catch (Exception e) {
			throw e;
		}
	}

}
