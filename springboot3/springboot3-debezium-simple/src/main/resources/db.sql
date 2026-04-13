CREATE TABLE "public"."t_user" (
                                   "id" int8 NOT NULL,
                                   "name" varchar(255),
                                   "age" int2,
                                   PRIMARY KEY ("id")
);

INSERT INTO public.t_user(id, "name", age) VALUES(1, 'harries', 18);