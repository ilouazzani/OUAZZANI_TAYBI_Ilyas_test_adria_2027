"""
=============================================================================
Détection de Fraude Bancaire - Machine Learning (scikit-learn)
Épreuve Technique ADRIA / ENSET 2027 - Partie H
=============================================================================
"""

import sys
import os
import pandas as pd
import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    precision_score,
    recall_score,
    f1_score,
    average_precision_score
)

def run_fraud_detection():
    data_path = os.path.join(os.path.dirname(__file__), "bank_transactions.csv")
    print("=" * 70)
    print(" 1. CHARGEMENT DES DONNÉES BANCAIRES")
    print("=" * 70)
    df = pd.read_csv(data_path)
    print(f"Jeu de données chargé avec succès : {df.shape[0]} lignes, {df.shape[1]} colonnes.\n")
    print("Aperçu des 3 premières transactions :")
    print(df.head(3).to_string())

    print("\n" + "=" * 70)
    print(" 2. PROPORTION DE FRAUDES & VALEURS MANQUANTES")
    print("=" * 70)
    fraud_counts = df['is_fraud'].value_counts()
    fraud_pct = df['is_fraud'].value_counts(normalize=True) * 100
    print("Distribution de la cible (is_fraud) :")
    for cls in [0, 1]:
        print(f"  Classe {cls} ({'Légitime' if cls == 0 else 'Fraude'}) : {fraud_counts.get(cls, 0)} ({fraud_pct.get(cls, 0):.2f}%)")

    missing = df.isnull().sum()
    print("\nValeurs manquantes par colonne :")
    print(missing[missing > 0].to_string() if missing.sum() > 0 else "  Aucune valeur manquante détectée.")

    print("\n" + "=" * 70)
    print(" 3 & 4. EXCLUSION DES IDENTIFIANTS & SÉPARATION STRATIFIÉE")
    print("=" * 70)
    # Exclure les identifiants
    features = [c for c in df.columns if c not in ['transaction_id', 'account_id', 'is_fraud']]
    X = df[features]
    y = df['is_fraud']

    # Séparation train/test stratifiée (80% train, 20% test)
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.20, random_state=42, stratify=y
    )
    print(f"Features sélectionnées : {features}")
    print(f"Taille de l'ensemble d'entraînement : {X_train.shape[0]} échantillons")
    print(f"Taille de l'ensemble de test : {X_test.shape[0]} échantillons")
    print(f"Proportion de fraudes train : {y_train.mean()*100:.2f}% | test : {y_test.mean()*100:.2f}% (Stratification respectée)")

    print("\n" + "=" * 70)
    print(" 5. CONSTRUCTION DU PIPELINE DE PRÉTRAITEMENT")
    print("=" * 70)
    num_features = ['amount', 'hour', 'transactions_last_24h', 'average_amount_last_7d']
    cat_features = ['country', 'merchant_category']

    # Prétraitement numérique : Imputation médiane + Normalisation standard
    num_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='median')),
        ('scaler', StandardScaler())
    ])

    # Prétraitement catégoriel : Imputation mode + One-Hot Encoding
    cat_transformer = Pipeline(steps=[
        ('imputer', SimpleImputer(strategy='most_frequent')),
        ('onehot', OneHotEncoder(handle_unknown='ignore', sparse_output=False))
    ])

    preprocessor = ColumnTransformer(transformers=[
        ('num', num_transformer, num_features),
        ('cat', cat_transformer, cat_features)
    ])

    print("Pipeline configuré : Imputation médiane + Standardisation (numériques), One-Hot Encoder (catégories).")

    print("\n" + "=" * 70)
    print(" 6. ENTRAÎNEMENT DU MODÈLE (RANDOM FOREST)")
    print("=" * 70)
    # Pipeline complet modèle
    model_pipeline = Pipeline(steps=[
        ('preprocessor', preprocessor),
        ('classifier', RandomForestClassifier(
            n_estimators=100,
            max_depth=6,
            class_weight='balanced',
            random_state=42
        ))
    ])

    # Ajustement uniquement sur les données d'entraînement (Data leakage évité)
    model_pipeline.fit(X_train, y_train)
    print("Modèle Random Forest entraîné avec succès sur l'ensemble d'entraînement.")

    print("\n" + "=" * 70)
    print(" 7. ÉVALUATION DU MODÈLE SUR L'ENSEMBLE DE TEST")
    print("=" * 70)
    y_pred = model_pipeline.predict(X_test)
    y_probs = model_pipeline.predict_proba(X_test)[:, 1]

    # Métriques
    prec = precision_score(y_test, y_pred, pos_label=1)
    rec = recall_score(y_test, y_pred, pos_label=1)
    f1 = f1_score(y_test, y_pred, pos_label=1)
    avg_prec = average_precision_score(y_test, y_probs)
    cm = confusion_matrix(y_test, y_pred)

    print("\n--- MATRICE DE CONFUSION ---")
    print(f" Vrais Négatifs (TN)  : {cm[0, 0]} | Faux Positifs (FP) : {cm[0, 1]}")
    print(f" Faux Négatifs (FN)   : {cm[1, 0]}  | Vrais Positifs (TP) : {cm[1, 1]}")

    print("\n--- MÉTRIQUES CLASSE FRAUDE (is_fraud = 1) ---")
    print(f"  • Précision (Precision)   : {prec:.4f} ({prec*100:.2f}%)")
    print(f"  • Rappel (Recall)         : {rec:.4f} ({rec*100:.2f}%)")
    print(f"  • F1-Score                : {f1:.4f}")
    print(f"  • Average Precision (PR)  : {avg_prec:.4f}")

    print("\n--- RAPPORT DE CLASSIFICATION DÉTAILLÉ ---")
    print(classification_report(y_test, y_pred, target_names=['Légitime (0)', 'Fraude (1)']))

    return {
        "precision": prec,
        "recall": rec,
        "f1": f1,
        "average_precision": avg_prec,
        "confusion_matrix": cm.tolist()
    }

if __name__ == "__main__":
    run_fraud_detection()
