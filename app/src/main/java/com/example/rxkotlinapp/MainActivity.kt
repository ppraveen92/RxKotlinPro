package com.example.rxkotlinapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction

import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)





        //JUST
        just.setOnClickListener {
            val listJus = listOf("Ram", "Ganesh", "Shiv")
            textviewInput.text=listJus.toString()
            Observable.just(listJus)
                .subscribe(
                    { value -> textviewOutput.text =value.toString()}, // onNext
                    { error -> Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show()  },    // onError
                    { Toast.makeText(this,"completed",Toast.LENGTH_SHORT).show() } // onComplete
                )
        }

        Observable.just("Hi I am RX")
            .subscribe { value ->  Log.v("Observable.just","Received: $value")}



        //JUST with ERROR

        justwitherror.setOnClickListener {
            val listJus = listOf("Manu", "Sonu", "Neenu")
            textviewInput.text=listJus.toString()
            Observable.just(listJus)
                .map({ input -> throw RuntimeException() } )
                .subscribe(
                    { value ->
                        Log.v("Observable.justError","Received: $value") },
                    { error ->Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show() },
                    { Log.v("Observable.justError","Completed") }
                )

        }


        //fromArray

        fromArray.setOnClickListener {
            val lis =listOf("praveen", "prasad", "praseetha").toString()
            textviewInput.text= lis
            Observable.fromArray("praveen", "prasad", "praseetha")
                .filter{it -> it.toString().endsWith("n")}
                .subscribeOn(Schedulers.newThread())
                // .subscribe{onNext -> println(onNext)}
                .subscribe({ onNext -> textviewOutput.text=onNext},
                    { error -> print(error)},
                    { print("complete")})
        }



        //fromIterable

        fromIter.setOnClickListener {
            val list = listOf ("praveen", "prasad", "praseetha").toString()
            textviewInput.text = list
            Observable. fromIterable ( listOf ("praveen", "prasad", "praseetha"))
                .subscribe (
                    { textviewInput.text=it
                    print(it)}, // onNext
                    {e -> println ("Error")}, // onError
                    { println ("Complete ")} // onComplete
                )
        }



        //create with method and shows eerror if its  windows

        getObservableFromList(listOf("android", "ios", "windows"))
            .subscribe ({value -> println("Received: $value") },
                { error -> Log.v("Received error",""+error)})



        //interval
        intervalRangen.setOnClickListener {

            Observable.intervalRange(
                10L,     // Start
                5L,      // Count
                0L,      // Initial Delay
                1L,      // Period
                TimeUnit.SECONDS
            ).subscribe ({ value ->textviewOutput.text=value.toString()
                         print(value)},
                {e -> Log.v("ObservableintervalRange","error "+e) },
                {Log.v("ObservableintervalRange","complete") })

        }


        //interval with infinite

        intervalInfi.setOnClickListener {

            Observable.interval(1000, TimeUnit.MILLISECONDS)
                .subscribe { textviewOutput.text=it.toString()
                print(it)}
        }




        //OutOfMemoryException since has back pressure

        val observable = PublishSubject.create<Int>()
        observable.observeOn(Schedulers.computation())
            .subscribe (
                {
                    println("The Number Is: $it")
                },
                {t->
                    print(t.message)
                }
            )
//        for (i in 0..1000000){
//            observable.onNext(i)
//        }


        //solution to use Flowable
        val observableWithSolution = PublishSubject.create<Int>()
        observableWithSolution
            .toFlowable(BackpressureStrategy.DROP)
            .observeOn(Schedulers.computation())
            .subscribe (
                {
                    println("The Number Is: $it")
                },
                {t->
                    print(t.message)
                }
            )
        for (i in 0..1000000){
            observableWithSolution.onNext(i)
        }


        //Differrnt kinds of Emitters which are the solution for backpressure
        //Flowable

        flowable.setOnClickListener {
            textviewInput.text="I am  Flowable"
            Flowable.just("I am  Flowable")
                .subscribe(
                    { value ->
                        textviewOutput.text=value},
                    { error -> println("Error: $error") },
                    { println("Completed") }
                )
        }


        //Maybe : used  to return a single optional value
        MayBe.setOnClickListener {
            textviewInput.text="I am  Maybe"
            Maybe.just("I am  Maybe")
            .subscribe(
                { value -> textviewOutput.text=value},
                { error -> println("Error: $error") },
                { println("Completed") }
            ) }


        //Single : used when there’s a single value to be returned


        Single.just("This is a Single")
            .subscribe(
                { v -> println("Value is: $v") },
                { e -> println("Error: $e")}
            )



        //Completable : A completable won’t emit any data, what it does is let you know whether the operation was successfully completed

       val com=Completable.create { emitter ->
            emitter.onComplete()
            emitter.onError(Exception())
        }

        com.doOnComplete{ print("dooonnnnnn")}



        Observable.just("Hello")
            .doOnSubscribe { println("Subscribed") }
            .doOnNext { s -> println("Received: $s") }
            .doAfterNext { println("After Receiving") }
            .doOnError { e -> println("Error: $e") }
            .doOnComplete { println("Complete") }
            .doFinally { println("Do Finally!") }
            .doOnDispose { println("Do on Dispose!") }
            .subscribe { println("Subscribe") }



        //SubscribeOn ObserveOn
        //SubscribeOn
        //Scheduler.io() :most common types of Scheduler that are used. generally used for IO related stuff, such as network requests, file system operations
        Observable.just("Sam", "Ram", "Kam")
            .subscribeOn(Schedulers.io())
            .subscribe{ v -> println("Received: $v") }



        //observeon : the observing thread in Android is the Main UI thread
        Observable.just("Apple", "Orange", "Banana")
            .subscribeOn(Schedulers.io())
          //  .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ v -> println("Received: $v") }



        //Operatorsssssss
        //map()
        //Transforms values emitted by an Observable stream into a single value . emits the modified items

        Observable.just("Water", "Fire", "Wood")
            .subscribeOn(Schedulers.io())
          //  .observeOn(AndroidSchedulers.mainThread())
            .map { m -> m + " 2" }
            .subscribe { v -> println("Received: $v") }

        //flatMap() will transform each value in an Observable stream into another Observable

        fflatMap.setOnClickListener {
            textviewInput.text= listOf("red","black","orange").toString()
            Observable.just("red","black","orange")
                .subscribeOn(Schedulers.io())
                //  .observeOn(AndroidSchedulers.mainThread())
                .flatMap { m ->
                    Observable.just(m + " 2")
                        .subscribeOn(Schedulers.io())
                }
                .subscribe { v -> textviewOutput.text=v
                print(v)}
        }






        //zip() operator will combine the values of multiple Observable together through a specific function

        Observable.zip(
            Observable.just(
                "Roses", "Sunflowers", "Leaves", "Clouds", "Violets", "Plastics"),
            Observable.just(
                "Red", "Yellow", "Green", "White or Grey", "Purple"),
            BiFunction<String, String, String> { type, color ->
                "$type are $color"
            }
        )
            .subscribe { v -> println("Received: $v") }



        //concat()
        //concatenate two or more Observable

        concat.setOnClickListener {

            textviewInput.text = listOf("Apple", "Orange", "Banana").toString()+"\n"+listOf("Microsoft", "Google").toString()+"\n"+listOf("Grass", "Tree", "Flower", "Sunflower").toString()
            val test1 = Observable.just("Apple", "Orange", "Banana")
            val test2 = Observable.just("Microsoft", "Google")
            val test3 = Observable.just("Grass", "Tree", "Flower", "Sunflower")

            Observable.concat(test1, test2, test3)
                .subscribe{ x ->
                    textviewOutput.text=x
                    print(x)
                }
        }





        //merge() works similarly to concat() , except merge will intercalate the emissions from both Observable , whereas concat() will wait for one to finish to show another.

        Observable.merge(
            Observable.interval(250, TimeUnit.MILLISECONDS).map { i -> "Apple" },
            Observable.interval(150, TimeUnit.MILLISECONDS).map { i -> "Orange" })
            .take(10)
            .subscribe{ v -> println("Received: $v") }

        //Filter()  to a set condition.

        Observable.just(2, 30, 22, 5, 60, 1)
            .filter{ x -> x < 10 }
            .subscribe{ x -> println("Received: " + x) }


        //repeat()
        //This operator will repeat the emission of the values as per the mentioned.

        Observable.just("Apple", "Orange", "Banana")
            .repeat(2)
            .subscribe { v -> println("Received: $v") }


        //take() operator is meant to grab however many emissions you’d like

        Observable.just("Apple", "Orange", "Banana")
            .take(2)
            .subscribe { v -> println("Received: $v") }


        //disposable

        Observable.just("Apple", "Orange", "Banana")
            .subscribe(
                { v -> println("Received: $v") }
            ).dispose()


        val compositeDisposable = CompositeDisposable()

        val observableOne = Observable.just("Tree")
            .subscribe { v -> println("Received: $v") }
        val observableTwo = Observable.just("Blue")
            .subscribe { v -> println("Received: $v") }

        compositeDisposable.add(observableOne)
        compositeDisposable.add(observableTwo)
        compositeDisposable.clear()


    }



}
//will create an Observableof the type string
fun getObservableFromList(myList: List<String>) =
    Observable.create<String> { emitter ->
        myList.forEach { item ->
            if (item == "windows") {
                emitter.onError(Exception("Errrorrrrrrrr"))
            }
            emitter.onNext(item)
        }
        emitter.onComplete()
    }