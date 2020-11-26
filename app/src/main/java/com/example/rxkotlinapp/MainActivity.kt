package com.example.rxkotlinapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listJus = listOf("Ram", "Ganesh", "Shiv")
        Observable.just(listJus)
            .subscribe(
                { value -> Log.v("Observable.just", "Received: $value") },
                { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                },
                { Toast.makeText(this, "completed", Toast.LENGTH_SHORT).show() }
            )

        Observable.just("Hi I am RX")
            .subscribe { value -> Log.v("Observable.just", "Received: $value") }

        val listJus2 = listOf("Manu", "Sonu", "Neenu")
        Observable.just(listJus2)
            .map({ input -> throw RuntimeException() })
            .subscribe(
                { value ->
                    Log.v("Observable.just", "Received: $value")
                },
                { error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show() },
                { Log.v("Observable.justError", "Completed") }
            )

        Observable.fromArray("praveen", "prasad", "praseetha")
            .filter { it.toString().endsWith("n") }
            .subscribeOn(Schedulers.newThread())
            .subscribe({ onNext -> Log.v("Observable.fromarray", "Received: $onNext") },
                { error -> print(error) },
                { print("complete") })

        Observable.fromIterable(listOf("praveen", "prasad", "praseetha"))
            .subscribe(
                {
                    Log.v("Observable.justError", "Received: $it")
                },
                { e -> println("Error") },
                { println("Complete ") }
            )

        getObservableFromList(listOf("android", "ios", "windows"))
            .subscribe({ value -> println("Received: $value") },
                { error -> Log.v("Received error", "" + error) })

        Observable.intervalRange(
            10L,
            5L,
            0L,
            1L,
            TimeUnit.SECONDS
        ).subscribe({ value ->
            Log.v(TAG, "Received: $value")
            print(value)
        },
            { e -> Log.v("ObservableintervalRange", "error " + e) },
            { Log.v("ObservableintervalRange", "complete") })


        Observable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                Log.v("Observable.interval", "Received: $it")
            }


        val observable = PublishSubject.create<Int>()
        observable.observeOn(Schedulers.computation())
            .subscribe(
                {
                    Log.v("Observable.observeon", "Received: $it")
                },
                { t ->
                    print(t.message)
                }
            )

        val observableWithSolution = PublishSubject.create<Int>()
        observableWithSolution
            .toFlowable(BackpressureStrategy.DROP)
            .observeOn(Schedulers.computation())
            .subscribe(
                {
                    println("The Number Is: $it")
                },
                { t ->
                    print(t.message)
                }
            )
        for (i in 0..1000000) {
            observableWithSolution.onNext(i)
        }

        Flowable.just("I am  Flowable")
            .subscribe(
                { value ->
                    Log.v(TAG, "Received: $value")
                },
                { error -> println("Error: $error") },
                { println("Completed") }
            )

        Maybe.just("I am  Maybe")
            .subscribe(
                { value -> Log.v(TAG, "Received: $value") },
                { error -> println("Error: $error") },
                { println("Completed") }
            )

        Single.just("This is a Single")
            .subscribe(
                { v -> println("Value is: $v") },
                { e -> println("Error: $e") }
            )

        Observable.just("Hello")
            .doOnSubscribe { println("Subscribed") }
            .doOnNext { s -> println("Received: $s") }
            .doAfterNext { println("After Receiving") }
            .doOnError { e -> println("Error: $e") }
            .doOnComplete { println("Complete") }
            .doFinally { println("Do Finally!") }
            .doOnDispose { println("Do on Dispose!") }
            .subscribe { println("Subscribe") }

        Observable.just("Sam", "Ram", "Kam")
            .subscribeOn(Schedulers.io())
            .subscribe { v -> println("Received: $v") }

        Observable.just("Apple", "Orange", "Banana")
            .subscribeOn(Schedulers.io())
            .subscribe { v -> println("Received: $v") }


        Observable.just("Water", "Fire", "Wood")
            .subscribeOn(Schedulers.io())
            .map { m -> m + " 2" }
            .subscribe { v -> println("Received: $v") }


        Observable.just("red", "black", "orange")
            .subscribeOn(Schedulers.io())
            .flatMap { m ->
                Observable.just(m + " 2")
                    .subscribeOn(Schedulers.io())
            }
            .subscribe { v ->
                Log.v(TAG, "Received: $v")
                print(v)
            }

        Observable.zip(
            Observable.just(
                "Roses", "Sunflowers", "Leaves", "Clouds", "Violets", "Plastics"
            ),
            Observable.just(
                "Red", "Yellow", "Green", "White or Grey", "Purple"
            ),
            { type, color ->
                "$type are $color"
            }
        ).subscribe { v -> println("Received: $v") }

        val test1 = Observable.just("Apple", "Orange", "Banana")
        val test2 = Observable.just("Microsoft", "Google")
        val test3 = Observable.just("Grass", "Tree", "Flower", "Sunflower")

        Observable.concat(test1, test2, test3)
            .subscribe { x ->
                Log.v(TAG, "Received: $x")
            }


        Observable.merge(
            Observable.interval(250, TimeUnit.MILLISECONDS).map { i -> "Apple" },
            Observable.interval(150, TimeUnit.MILLISECONDS).map { i -> "Orange" })
            .take(10)
            .subscribe { v -> println("Received: $v") }

        Observable.just(2, 30, 22, 5, 60, 1)
            .filter { x -> x < 10 }
            .subscribe { x -> println("Received: " + x) }

        Observable.just("Apple", "Orange", "Banana")
            .repeat(2)
            .subscribe { v -> println("Received: $v") }

        Observable.just("Apple", "Orange", "Banana")
            .take(2)
            .subscribe { v -> println("Received: $v") }

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